package ru.sfedu.studyProject.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Metadata util.
 */
public class MetadataUtil {

  /**
   * The type Metadata.
   */
  public static class Metadata{

  }

  private static final Map<Class, Metadata> metadataMap = new HashMap<>();

  private MetadataUtil(){

  }

  /**
   * Gets metadata instance.
   *
   * @param <T>    the type parameter
   * @param tClass the t class
   * @return the metadata
   */
  public static <T> Metadata getMetadata(Class<T> tClass) {
    if (!metadataMap.containsKey(tClass)) {
      metadataMap.put(tClass, new Metadata());
    }
    return metadataMap.get(tClass);
  }


}
