package me.dio.dio_springboot_project.core.util;

import java.util.Collection;

public interface CollectionAdders<E> {
  boolean add(E e);
  boolean addAll(Collection<? extends E> c);
}

