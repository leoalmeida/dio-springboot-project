package me.dio.dio_springboot_project.controller;

import me.dio.dio_springboot_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import me.dio.dio_springboot_project.domain.model.Usuario;
import me.dio.dio_springboot_project.domain.repository.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    UserService userService;

    @PostMapping("")
    public void post(@RequestBody Usuario usuario){
        userService.criarUsuario(usuario);
    }
    @PutMapping("")
    public void put(@RequestBody Usuario usuario){
        userService.alterarUsuario(usuario);
    }
    @GetMapping("{id}")
    public Usuario getOne(@PathVariable("id") Integer id){
        return userService.consultarUsuario(id);
    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Integer id){
        userService.removerUsuario(id);
    }
    @GetMapping("")
    public List<Usuario> getAll(){
        return userService.listarUsuarios();
    }
}

