package bfst21.file_io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProgressOutputStream extends FilterOutputStream {
    private StreamListener streamListener;
    private long totalBytes = 0;

    public ProgressOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    public void addInputStreamListener(StreamListener streamListener) {
        this.streamListener = streamListener;
    }

    @Override
    public void write(int b) throws IOException {
        totalBytes += b;
        streamListener.onBytesTouched(totalBytes);
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        totalBytes += len;
        streamListener.onBytesTouched(totalBytes);
        out.write(b, off, len);
    }
}