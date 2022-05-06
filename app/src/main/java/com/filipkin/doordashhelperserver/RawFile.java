package com.filipkin.doordashhelperserver;

import android.content.res.Resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RawFile {

    private int id;
    private byte[] value;

    public RawFile(int id) throws IOException {
        this.id = id;
        readFile();
    }

    public byte[] readFile() throws IOException {
        InputStream is = Resources.getSystem().openRawResource(this.id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1];
        while (is.read(b) != -1) {
            baos.write(b);
        }
        byte[] res = baos.toByteArray();
        is.close();
        baos.close();
        value = res;
        return res;
    }

    public byte[] getValue() {
        return value;
    }
}
