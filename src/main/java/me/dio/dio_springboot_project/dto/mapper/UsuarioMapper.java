package me.dio.dio_springboot_project.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import me.dio.dio_springboot_project.domain.model.Usuario;
import me.dio.dio_springboot_project.dto.UsuarioDto;

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

		List<UsuarioDto> list = new ArrayList<UsuarioDto>();

		if(usuarioEntities != null) {
			usuarioEntities.stream().forEach(usuario -> {
				list.add(UsuarioMapper.toUsuarioDto(usuario));
			});
		}

		return list;
	}

	public static List<Usuario> toListUsuarioEntity(List<UsuarioDto> usuarioDtos) {

		List<Usuario> list = new ArrayList<Usuario>();

		if(usuarioDtos != null) {
			usuarioDtos.stream().forEach(usuario -> {
				list.add(UsuarioMapper.toUsuarioEntity(usuario));
			});
		}

		return list;
	}
}