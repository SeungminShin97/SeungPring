package org.example.framework.was.protocol.core;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.model.HttpMessage;

import java.io.InputStream;

public interface RequestParser {

    HttpMessage parse(InputStream inputStream) throws HttpParsingException;
}
