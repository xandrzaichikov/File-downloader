package test.filedownloader.io;

import com.google.common.util.concurrent.RateLimiter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ThrottledInputStream extends FilterInputStream {
    private final RateLimiter rateLimiter;

    public ThrottledInputStream(final InputStream in, final long bytesPerSecond) {
        super(in);
        this.rateLimiter = RateLimiter.create(bytesPerSecond);
    }

    @Override
    public int read() throws IOException {
        final int b = super.read();
        if (b > -1) {
            throttle(1);
        }

        return b;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytesRead = super.read(b, off, len);
        if (bytesRead > 0) {
            throttle(bytesRead);
        }

        return bytesRead;
    }

    private void throttle(final int bytes) {
        if (bytes == 1) {
            this.rateLimiter.acquire();
        } else if (bytes > 1) {
            // acquire blocks based on previously acquired permits. If multiple bytes read, call
            // acquire twice so throttling occurs even if read is only called once (small files)
            this.rateLimiter.acquire(bytes - 1);
            this.rateLimiter.acquire();
        }
    }

    @Override
    public String toString() {
        return String.format("ThrottledInputStream [in=%s, bytesPerSecond=%s]", this.in,
                this.rateLimiter.getRate());
    }
}