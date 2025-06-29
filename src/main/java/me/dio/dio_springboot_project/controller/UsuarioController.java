package me.dio.dio_springboot_project.controller;

import me.dio.dio_springboot_project.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import me.dio.dio_springboot_project.dto.UsuarioDto;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    public void post(@RequestBody UsuarioDto usuarioDto){
        usuarioService.criarUsuario(usuarioDto);
    }
    @PutMapping
    public void put(@RequestBody UsuarioDto usuarioDto){
        usuarioService.alterarUsuario(usuarioDto);
    }
    @GetMapping("{id}")
    public UsuarioDto getOne(@PathVariable("id") String id){
        return usuarioService.consultarUsuario(id);
    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id){
        usuarioService.removerUsuario(id);
    }
    @GetMapping
    public List<UsuarioDto> getAll(){
        return usuarioService.listarUsuarios();
    }
}

