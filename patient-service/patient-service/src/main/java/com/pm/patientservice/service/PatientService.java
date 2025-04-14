package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }


    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();

    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        String email = patientRequestDTO.getEmail();
        if(patientRepository.existsByEmail(email)){
            throw new EmailAlreadyExistException("A patient of this email :"+email +" already exists");
        }

        Patient savedPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        // an email should be unique
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow(() ->  new PatientNotFoundException("Patient not found for id: " + id));
        String email = patientRequestDTO.getEmail();
        if(patientRepository.existsByEmailAndIdNot(email, id)){
            throw new EmailAlreadyExistException("A patient of this email :"+email +" already exists");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(email);
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletPatient(UUID id){
        patientRepository.deleteById(id);
    }
}
