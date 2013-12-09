package ru.st.selenium.wait;

import com.google.common.base.Function;

import java.util.List;

public interface RepeatableAction <T, V> extends Function<T, V> {

  public List<Class<? extends Throwable>> ignoredExceptions();

}
