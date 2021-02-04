package dev.soterocra.demo.controller;

import dev.soterocra.demo.model.Student;
import dev.soterocra.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentsController {

    private final StudentRepository repository;

    @PostMapping
    public Mono<ResponseEntity<Student>> create(@RequestBody Student student) {
        return repository.save(student)
              .map(savedStudent -> ResponseEntity.ok(savedStudent))
              .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Student> getStudents(){
        return repository.findAll();
    }

    @GetMapping("/{studentId}")
    public Mono<ResponseEntity<Student>> getStudentById(@PathVariable String studentId){
        return repository.findById(studentId)
              .map(student -> ResponseEntity.ok(student))
              .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{studentId}")
    public Mono<ResponseEntity<Student>> updateStudent(@PathVariable String studentId, @RequestBody Student student){
        return repository.findById(studentId)
                .flatMap(selectedStudentFromDB -> {
                    selectedStudentFromDB.setName(student.getName());
                    selectedStudentFromDB.setAge(student.getAge());
                    selectedStudentFromDB.setUniversity(student.getUniversity());

                    return repository.save(selectedStudentFromDB);
                })
                .map(updatedStudent -> ResponseEntity.ok(updatedStudent))
                .defaultIfEmpty(new ResponseEntity<Student>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStudent(@PathVariable(value = "id") String studentId) {

        return repository.findById(studentId)
              .flatMap(selectedStudentFromDB ->
                    repository.delete(selectedStudentFromDB)
                          .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
              )
              .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
