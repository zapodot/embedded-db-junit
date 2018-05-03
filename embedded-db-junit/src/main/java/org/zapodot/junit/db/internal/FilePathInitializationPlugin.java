package org.zapodot.junit.db.internal;

import org.h2.store.fs.FilePath;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class FilePathInitializationPlugin implements InitializationPlugin {

    private static final int BUFFER_SIZE = 8192;

    private final String resource;

    private final Charset charset;

    public FilePathInitializationPlugin(final String resource, final Charset charset) {
        if (null == resource) {
            throw new IllegalArgumentException("The \"resource\" parameter must be provided");
        }
        this.resource = resource;
        if (null == charset) {
            throw new IllegalArgumentException("The \"charset\" parameter must be provided");
        }
        this.charset = charset;
    }

    @Override
    public void connectionMade(final String name, final Connection connection) {
        final FilePath filePath = FilePath.get(resource);
        try (final InputStream inputStream = filePath.newInputStream();
             final Statement statement = connection.createStatement();
             final ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, inputStream.available()));
        ) {
            copyToOutputStream(inputStream, out);
            statement.execute(convertToString(out.toByteArray()));

        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not read SQL from file path \"%s\"",
                                                             filePath.getName()),
                                               e);
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Could not run SQL script from file path \"%s\"",
                                                             filePath.getName()), e);
        }

    }

    private String convertToString(final byte[] buffer) {
        return new String(buffer, charset);
    }

    private void copyToOutputStream(final InputStream inputStream, final ByteArrayOutputStream out) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            int readInOperation = inputStream.read(buffer);
            if (readInOperation == -1) {
                break;
            }
            out.write(buffer, 0, readInOperation);
        }
    }

}
