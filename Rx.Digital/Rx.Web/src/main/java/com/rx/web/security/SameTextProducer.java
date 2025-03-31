package com.rx.web.security;

import nl.captcha.text.producer.TextProducer;

public class SameTextProducer implements TextProducer {

    private final String text;

    public SameTextProducer(String text) {
        this.text = text;
    }

    @Override
	public String getText() {
		return this.text;
    }

}
