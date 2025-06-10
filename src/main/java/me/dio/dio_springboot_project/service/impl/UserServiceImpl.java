package me.dio.dio_springboot_project.service.impl;

import me.dio.dio_springboot_project.domain.model.Usuario;
import me.dio.dio_springboot_project.domain.repository.UsuarioRepository;
import me.dio.dio_springboot_project.handler.BusinessException;
import me.dio.dio_springboot_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario criarUsuario(Usuario novoUsuario){
        if(novoUsuario.getLogin()==null || novoUsuario.getLogin().isEmpty())
            throw new BusinessException("O campo ID precisa ser informado");

        return repository.save(novoUsuario);
    }

    public Boolean alterarUsuario(Usuario usuario){
        if(usuario.getLogin()==null || usuario.getLogin().isEmpty())
            throw new BusinessException("O campo ID precisa ser informado");

        return repository.update(usuario);
    }

    public Boolean removerUsuario(Integer id){
        if(id==null || id < 0)
            throw new BusinessException("O campo ID precisa ser informado");

        return repository.remove(id);
    }
    
    public List<Usuario> listarUsuarios(){
        return repository.listAll();
    }

    public Usuario consultarUsuario(Integer id){
        if(id==null || id < 0)
            throw new BusinessException("O campo ID precisa ser informado");
        else
            return repository.findById(id);
    }
}