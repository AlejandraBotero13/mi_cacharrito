package mi_cacharrito.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mi_cacharrito.modelo.Administrador;

@Repository
public interface administrador extends JpaRepository<Administrador, String>{

}
