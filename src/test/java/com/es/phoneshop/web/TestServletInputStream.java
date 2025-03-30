package com.es.phoneshop.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    public TestServletInputStream(String json) {
        this.byteArrayInputStream = new ByteArrayInputStream(json.getBytes());
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }

    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }
}
