package com.CRUD.firstApp.instructors;


import org.apache.catalina.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorsService {

    private final InstructorsRepository instructorsRepository;
    private final InstructorsMapper instructorsMapper;

    public InstructorsService(InstructorsRepository instructorsRepository, InstructorsMapper instructorsMapper) {
        this.instructorsRepository = instructorsRepository;
        this.instructorsMapper = instructorsMapper;
    }


    public List<InstructorsResponce> getAllInstructors() {
        return instructorsRepository.findAll()
                .stream()
                .map(instructorsMapper::toResponce)
                .collect(Collectors.toList());
    }

    public Instructors getInstructorById(int id) {
        return   instructorsRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Instructor not found with id "+ id));


    }

    public List<InstructorsResponce> getInstructorsByName(String name) {
        var IntrucorsExsistng = instructorsRepository.findByFirstName(name);
        if (IntrucorsExsistng.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Instructor not found with name "+ name);
        }
        return IntrucorsExsistng.stream()
                .map(instructorsMapper::toResponce)
                .collect(Collectors.toList());
    }

    public InstructorsResponce addInstructor(InstructorsRequest request) {
      var InstructorToEntity = instructorsMapper.toIntructors(request);
      instructorsRepository.save(InstructorToEntity);
      return instructorsMapper.toResponce(InstructorToEntity);
    }

    public InstructorsResponce updateInstructor(int id, InstructorsRequest request) {
        var exsist = instructorsRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Instructor not found with id "+ id));

        if (StringUtils.hasText(request.firstName())){
            exsist.setFirstName(request.firstName());
        }
        if (StringUtils.hasText(request.lastName())){
            exsist.setLastName(request.lastName());
        }
        if (StringUtils.hasText(request.email())) {
            exsist.setEmail(request.email());
        }
        if (StringUtils.hasText(request.bio())) {
            exsist.setBio(request.bio());
        }
        if (StringUtils.hasText(request.profileImage())) {
            exsist.setProfileImage(request.profileImage());
        }

        instructorsRepository.save(exsist);
        return instructorsMapper.toResponce(exsist);
    }

    public void deleteInstructor(int id) {
         instructorsRepository.deleteById(id);
    }
}
