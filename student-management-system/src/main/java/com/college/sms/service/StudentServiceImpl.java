package com.college.sms.service;

import com.college.sms.dto.StudentRequestDTO;
import com.college.sms.dto.StudentResponseDTO;
import com.college.sms.entity.Student;
import com.college.sms.exception.DuplicateEmailException;
import com.college.sms.exception.ResourceNotFoundException;
import com.college.sms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        if (studentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException("Student with email '" + requestDTO.getEmail() + "' already exists.");
        }

        Student student = modelMapper.map(requestDTO, Student.class);
        Student savedStudent = studentRepository.save(student);
        return modelMapper.map(savedStudent, StudentResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(student -> modelMapper.map(student, StudentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentEntityById(id);
        return modelMapper.map(student, StudentResponseDTO.class);
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        Student existingStudent = findStudentEntityById(id);

        if (studentRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new DuplicateEmailException("Email '" + requestDTO.getEmail() + "' is already in use by another student.");
        }

        existingStudent.setName(requestDTO.getName());
        existingStudent.setEmail(requestDTO.getEmail());
        existingStudent.setPhoneNumber(requestDTO.getPhoneNumber());
        existingStudent.setDepartment(requestDTO.getDepartment());
        existingStudent.setYearOfStudy(requestDTO.getYearOfStudy());
        existingStudent.setCgpa(requestDTO.getCgpa());

        Student updatedStudent = studentRepository.save(existingStudent);
        return modelMapper.map(updatedStudent, StudentResponseDTO.class);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = findStudentEntityById(id);
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartmentIgnoreCase(department)
                .stream()
                .map(student -> modelMapper.map(student, StudentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsByCgpaGreaterThan(Double minCgpa) {
        return studentRepository.findByCgpaGreaterThan(minCgpa)
                .stream()
                .map(student -> modelMapper.map(student, StudentResponseDTO.class))
                .collect(Collectors.toList());
    }

    private Student findStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }
}
