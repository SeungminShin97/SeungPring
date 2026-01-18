package org.example.framework.was.protocol.core;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.model.HttpMessage;
import org.example.framework.was.protocol.model.HttpRequest;

import java.io.InputStream;

public interface RequestParser {

    HttpRequest parse(InputStream inputStream) throws HttpParsingException;
}
