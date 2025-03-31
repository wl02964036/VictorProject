package com.rx.web.interceptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import com.rx.web.security.CustomWebAuthenticationDetails;
import com.rx.web.security.SameTextProducer;

import lombok.extern.slf4j.Slf4j;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.audio.Sample;
import nl.captcha.text.producer.NumbersAnswerProducer;

@Slf4j
public class CaptchaInterceptor implements HandlerInterceptor {

	private String uploadRootPath;

	public CaptchaInterceptor(String uploadRootPath) {
		super();
		this.uploadRootPath = uploadRootPath;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("*** interceptor reload captcha ***");

		HttpSession session = request.getSession();

		synchronized (WebUtils.getSessionMutex(session)) {
			session.setAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName, null);
			session.removeAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName);

			NumbersAnswerProducer answerProducer = new NumbersAnswerProducer(4);
			String answer = answerProducer.getText();
			session.setAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName, answer);

			String wavPath = uploadRootPath + File.separator + "wav";

			if (answer != null) {
				AudioCaptcha ac = new AudioCaptcha.Builder().addAnswer(new SameTextProducer(answer)).build();

				Sample sample = ac.getChallenge();

				File audioFile = new File(wavPath, answer + ".wav");
				if (!audioFile.exists()) {
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

		return true;
	}

}