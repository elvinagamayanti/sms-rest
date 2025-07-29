package com.sms.unit.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sms.entity.Program;
import com.sms.repository.ProgramRepository;

@DataJpaTest
public class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;

    Program program;

    @BeforeEach
    void setUp() {
        program = new Program();
        program.setCode("TP01");
        program.setName("Test Program");
        program.setYear("2025");
        programRepository.save(program);
    }

    @AfterEach
    void tearDown() {
        program = null;
        programRepository.deleteAll();
    }

    // Test Success

    @Test
    public void testFindByName_Found() {
        Program foundProgram = programRepository.findByName("Test Program");
        assertThat(foundProgram).isNotNull();
        assertThat(foundProgram.getName()).isEqualTo(program.getName());
        assertThat(foundProgram.getCode()).isEqualTo(program.getCode());
        assertThat(foundProgram.getYear()).isEqualTo(program.getYear());
    }

    @Test
    public void testFindByCode_Found() {
        Optional<Program> foundProgram = programRepository.findByCode("TP01");
        assertThat(foundProgram.isPresent()).isTrue();
        assertThat(foundProgram.get().getName()).isEqualTo(program.getName());
        assertThat(foundProgram.get().getCode()).isEqualTo(program.getCode());
        assertThat(foundProgram.get().getYear()).isEqualTo(program.getYear());
    }

    // Test Failure

    @Test
    public void testFindByName_NotFound() {
        Program foundProgram = programRepository.findByName("Non Existent Program");
        assertThat(foundProgram).isNull();
    }

    @Test
    public void testFindByCode_NotFound() {
        Optional<Program> foundProgram = programRepository.findByCode("TP02");
        assertThat(foundProgram.isPresent()).isFalse();
    }
}
