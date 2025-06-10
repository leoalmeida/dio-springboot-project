package me.dio.dio_springboot_project.domain.repository;

import me.dio.dio_springboot_project.handler.BusinessException;
import me.dio.dio_springboot_project.domain.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepository {
    public Usuario save(Usuario usuario){
        if (usuario.getLogin()==null)
            throw new BusinessException("Campo login obrigatório");
        System.out.println("SAVE - Recebendo o usuário na camada de repositório");
        System.out.println(usuario);
        return usuario;
    }
    public Boolean update(Usuario usuario){
        System.out.println("UPDATE - Recebendo o usuário na camada de repositório");
        System.out.println(usuario);
        return true;
    }
    public Boolean remove(Integer id){
        System.out.println(String.format("DELETE/id - Recebendo o id: %d para excluir um usuário", id));
        System.out.println(id);
        return true;
    }
    public List<Usuario> listAll(){
        System.out.println("LIST - Listando os usários do sistema");
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario("gleyson","password"));
        usuarios.add(new Usuario("frank","masterpass"));
        return usuarios;
    }
    public Usuario findById(Integer id){
        System.out.println(String.format("FIND/id - Recebendo o id: %d para localizar um usuário", id));
        return new Usuario("gleyson","password");
    }

}
