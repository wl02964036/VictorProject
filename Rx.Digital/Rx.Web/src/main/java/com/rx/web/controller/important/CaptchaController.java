package com.rx.web.controller.important;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.rx.web.security.CustomWebAuthenticationDetails;
import com.rx.web.security.SameTextProducer;

import lombok.extern.slf4j.Slf4j;
import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.audio.Sample;
import nl.captcha.text.producer.NumbersAnswerProducer;

@Slf4j
@Controller
public class CaptchaController {

    @Value("${app.upload.path}")
    protected String uploadRootPath;

    public CaptchaController() {
        super();
    }

    private String getAnswer(final WebRequest request) {
        String answer;
        synchronized (request.getSessionMutex()) {
            answer = (String) request.getAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName, WebRequest.SCOPE_SESSION);
        }
        return answer;
    }

    // 取得驗證碼圖片，使用 openjdk 11 的話，要注意 linux 的字型問題
    @GetMapping(name = "captchaImage", path = "/captchaImage", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] captchaImage(final WebRequest request, final HttpServletResponse response) throws IOException {
        response.addHeader("Cache-Control", CacheControl.noCache().getHeaderValue());
        response.addHeader("Cache-Control", CacheControl.noStore().getHeaderValue());

        String answer = getAnswer(request);
        if (answer == null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.flush();
            stream.close();
            return stream.toByteArray();
        }

        Captcha captcha = new Captcha.Builder(96, 43)
                                     .addText(new SameTextProducer(answer))
                                     .build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(captcha.getImage(), "png", stream);
        stream.flush();
        stream.close();
        return stream.toByteArray();
    }

    @GetMapping(name = "captchaAudio", path = "/captchaAudio")
    public String captchaAudio(final WebRequest request, final HttpServletResponse response) throws IOException {
        // 直接導到儲存的檔案
        String answer = getAnswer(request);
        return "redirect:/wav/" + answer + ".wav";
    }

    @GetMapping(name = "reloadCaptcha", path = "/reloadCaptcha", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String reloadCaptcha(final WebRequest request, final HttpServletResponse response) {
        response.addHeader("Cache-Control", CacheControl.noCache().getHeaderValue());
        response.addHeader("Cache-Control", CacheControl.noStore().getHeaderValue());

        // 刪掉 session 中的 captcha，再重新產生 captcha
        synchronized (request.getSessionMutex()) {
            request.setAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName, null, WebRequest.SCOPE_SESSION);
            request.removeAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName, WebRequest.SCOPE_SESSION);

            NumbersAnswerProducer answerProducer = new NumbersAnswerProducer(4);
            String answer = answerProducer.getText();
            request.setAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName, answer, WebRequest.SCOPE_SESSION);

            String wavPath = uploadRootPath + File.separator + "wav";

            if (answer != null) {
                AudioCaptcha ac = new AudioCaptcha.Builder()
                        .addAnswer(new SameTextProducer(answer))
                        .build();

                Sample sample = ac.getChallenge();

                File audioFile = new File(wavPath, answer + ".wav");
                if (! audioFile.exists()) {
                    FileOutputStream stream = null;
                    try {
                        stream = new FileOutputStream(audioFile);
                        AudioSystem.write(sample.getAudioInputStream(), AudioFileFormat.Type.WAVE, stream);
                        stream.flush();
                    } catch (FileNotFoundException e) {
                    	log.error("", e);
                    } catch (IOException e) {
                        log.error("", e);
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                // ignore
                            }
                        }
                    }
                }
            }

        }
        return "succeed";
    }

    @GetMapping(name = "captchaNumber", path = "/captchaNumber",
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String captchaNumber(final WebRequest request) {
        String answer = getAnswer(request);
        if (StringUtils.isBlank(answer)) {
            return "0000";
        } else {
            return answer;
        }
    }

}
