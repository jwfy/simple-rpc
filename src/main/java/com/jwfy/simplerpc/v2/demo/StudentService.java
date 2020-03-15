package com.jwfy.simplerpc.v2.demo;

/**
 * @author jwfy
 */
public class StudentService implements IStudentService {

    @Override
    public String getName() {
        return "jwfy";
    }
}
