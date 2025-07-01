package space.lasf.springboot_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import space.lasf.springboot_project.dto.UsuarioDto;
import space.lasf.springboot_project.service.UsuarioService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    public UsuarioDto post(@RequestBody UsuarioDto usuarioDto){
        return usuarioService.criarUsuario(usuarioDto);
    }
    @PutMapping
    public UsuarioDto put(@RequestBody UsuarioDto usuarioDto){
        return usuarioService.alterarUsuario(usuarioDto);
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

