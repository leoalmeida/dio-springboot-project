package space.lasf.springboot_project.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import space.lasf.springboot_project.domain.model.Usuario;
import space.lasf.springboot_project.dto.UsuarioDto;

@Component
public class UsuarioMapper {
    
    public static UsuarioDto toUsuarioDto(Usuario usuario) {

		if (usuario == null)
			return null;

		return UsuarioDto.builder()
				.id(usuario.getId())
				.email(usuario.getEmail())
				.login(usuario.getLogin())
				.password(usuario.getPassword())
				.build();
	}
	
	public static Usuario toUsuarioEntity(UsuarioDto usuarioDto) {

		if (usuarioDto == null)
			return null;
		
		Usuario usuarioEntity = Usuario.builder()
				.id(usuarioDto.getId())
				.email(usuarioDto.getEmail())
				.login(usuarioDto.getLogin())
				.password(usuarioDto.getPassword())
				.build();

		return usuarioEntity;
	}
	
	public static List<UsuarioDto> toListUsuarioDto(List<Usuario> usuarioEntities) {

		if (usuarioEntities == null) {
			return new ArrayList<>();
		}
		return usuarioEntities.stream()
				.map(UsuarioMapper::toUsuarioDto)
				.toList();
	}

	public static List<Usuario> toListUsuarioEntity(List<UsuarioDto> usuarioDtos) {

		if (usuarioDtos == null) {
			return new ArrayList<>();
		}
		return usuarioDtos.stream()
				.map(UsuarioMapper::toUsuarioEntity)
				.toList();
	}
}