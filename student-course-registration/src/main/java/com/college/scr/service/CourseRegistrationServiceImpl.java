package com.college.scr.service;

import com.college.scr.dto.*;
import com.college.scr.entity.Course;
import com.college.scr.entity.Student;
import com.college.scr.exception.DuplicateEnrollmentException;
import com.college.scr.exception.DuplicateResourceException;
import com.college.scr.exception.ResourceNotFoundException;
import com.college.scr.repository.CourseRepository;
import com.college.scr.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseRegistrationServiceImpl implements CourseRegistrationService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public StudentResponseDTO registerStudent(StudentRequestDTO requestDTO) {
        if (studentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Student with email '" + requestDTO.getEmail() + "' already exists.");
        }

        Student student = Student.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .department(requestDTO.getDepartment())
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToStudentDTO(savedStudent);
    }

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        if (courseRepository.existsByCourseCode(requestDTO.getCourseCode())) {
            throw new DuplicateResourceException("Course with code '" + requestDTO.getCourseCode() + "' already exists.");
        }

        Course course = Course.builder()
                .courseCode(requestDTO.getCourseCode())
                .title(requestDTO.getTitle())
                .credits(requestDTO.getCredits())
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToCourseDTO(savedCourse);
    }

    @Override
    public StudentResponseDTO enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = findStudentWithCourses(studentId);
        Course course = findCourseById(courseId);

        if (student.getCourses().contains(course)) {
            throw new DuplicateEnrollmentException(String.format(
                    "Student '%s' is already enrolled in course '%s' (%s).",
                    student.getName(), course.getTitle(), course.getCourseCode()
            ));
        }

        student.addCourse(course);
        Student updatedStudent = studentRepository.save(student);
        return mapToStudentDTO(updatedStudent);
    }

    @Override
    public StudentResponseDTO removeEnrollment(Long studentId, Long courseId) {
        Student student = findStudentWithCourses(studentId);
        Course course = findCourseById(courseId);

        if (!student.getCourses().contains(course)) {
            throw new ResourceNotFoundException(String.format(
                    "Student '%s' is not enrolled in course '%s' (%s).",
                    student.getName(), course.getTitle(), course.getCourseCode()
            ));
        }

        student.removeCourse(course);
        Student updatedStudent = studentRepository.save(student);
        return mapToStudentDTO(updatedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long studentId) {
        Student student = findStudentWithCourses(studentId);
        return mapToStudentDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long courseId) {
        Course course = findCourseById(courseId);
        return mapToCourseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(int pageNo, int pageSize) {
        return studentRepository.findAll(PageRequest.of(pageNo, pageSize))
                .map(this::mapToStudentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> getAllCourses(int pageNo, int pageSize) {
        return courseRepository.findAll(PageRequest.of(pageNo, pageSize))
                .map(this::mapToCourseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CourseResponseDTO> getCoursesByStudentId(Long studentId) {
        Student student = findStudentWithCourses(studentId);
        return student.getCourses().stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<StudentResponseDTO> getStudentsByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        return course.getStudents().stream()
                .map(this::mapToStudentDTO)
                .collect(Collectors.toSet());
    }

    private Student findStudentWithCourses(Long studentId) {
        return studentRepository.findWithCoursesById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    private StudentResponseDTO mapToStudentDTO(Student student) {
        StudentResponseDTO dto = modelMapper.map(student, StudentResponseDTO.class);
        if (student.getCourses() != null) {
            Set<CourseResponseDTO> courseDTOs = student.getCourses().stream()
                    .map(this::mapToCourseDTO)
                    .collect(Collectors.toSet());
            dto.setCourses(courseDTOs);
        }
        return dto;
    }

    private CourseResponseDTO mapToCourseDTO(Course course) {
        return modelMapper.map(course, CourseResponseDTO.class);
    }
}
