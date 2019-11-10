package com.jwfy.simplerpc.v2.demo;

/**
 * @author junhong
 */
public class StudentService implements IStudentService {

    @Override
    public String getName() {
        return "jwfy";
    }
}
