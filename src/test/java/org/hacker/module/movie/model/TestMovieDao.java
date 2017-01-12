//package org.hacker.module.movie.model;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.hacker.core.config.PluginFactory;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class TestMovieDao {
//  
//  private Map<String, Object> paras = new HashMap<String, Object>() {{
//    put("mp4", KRandom.randomStr(10));
//    put("name", KRandom.randomStr(4));
//    put("rank", KRandom.randomInt(5));
//    put("summary", KRandom.randomStr(10));
//  }};
//  
//  private Integer id = null;
//  
//  @BeforeClass
//  public static void init() {
//    PluginFactory.startActiveRecordPlugin();
//  }
//  
//  @Test
//  public void test_create() {
//    Movie movie = new Movie();
//    movie.setMp4((String)paras.get("mp4"));
//    movie.setName((String)paras.get("name"));
//    movie.setRank((Integer)paras.get("rank"));
//    movie.setSummary((String)paras.get("summary"));
//    Assert.assertEquals(movie.save(), true);
//    id = movie.getId();
//  }
//  
//  public void test_retrieve() {
//    Movie movie = Movie.dao.findById(id);
//    Assert.assertEquals(movie.getMp4(), (String)paras.get("mp4"));
//  }
//  
//}
