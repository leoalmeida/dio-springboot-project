package me.dio.dio_springboot_project.domain.model;

import java.io.Serial;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection  = "usuarios")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    @NotNull
    private String email;
    @NotNull
    private String login;
    private String password;
    
}
