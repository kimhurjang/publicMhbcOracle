package com.example.mhbc;

import com.example.mhbc.dto.MemberMapper;
import com.example.mhbc.mapper.CustomMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MhbcApplicationTests {

  @Autowired
  CustomMapper customMapper;

  @Test
  void mybatisTest(){
    List<MemberMapper> list = customMapper.selectAll();
    for (MemberMapper m: list){
      System.out.println(m.getIdx());
      System.out.println(m.getName());
    }
  }

  @Test
  void mybatisTest2(){
    List<MemberMapper> list2 = customMapper.selectId("");
    for (MemberMapper m: list2){
      System.out.println(m.getIdx());
      System.out.println(m.getName());
    }
  }

	@Test
	void contextLoads() {
	}

}
