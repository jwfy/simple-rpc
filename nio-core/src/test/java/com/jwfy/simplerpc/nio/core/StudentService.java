package com.jwfy.simplerpc.nio.core;

/**
 * @author jwfy
 */
public class StudentService implements IStudentService {

    @Override
    public String getName() {
        return "jwfy";
    }
}
