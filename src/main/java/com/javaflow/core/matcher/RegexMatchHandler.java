package com.javaflow.core.matcher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import spark.utils.Assert;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class RegexMatchHandler implements MatchHandler<String, Object> {

    private final String pattern;

    private final Function<Matcher, Object> handler;

    @Getter(lazy = true)
    private final Pattern regexPattern = Pattern.compile(pattern);

    @Override
    public boolean match(String data) {
        return getRegexPattern().matcher(data).find();
    }

    @Override
    public Object handle(String data) {
        Matcher matcher = getRegexPattern().matcher(data);
        Assert.isTrue(matcher.find(), String.format("Must match regex: %s", pattern));
        return handler.apply(matcher);
    }

}
