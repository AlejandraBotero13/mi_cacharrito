package mi_cacharrito.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "automovil")
public class Automovil {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "placa", length = 6, nullable = false)
    private String placa;

    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @Column(name = "modelo", nullable = false)
    private int modelo;

    @Column(name = "marca", length = 25, nullable = false)
    private String marca;

    public Automovil(int id, String placa, int capacidad, int modelo, String marca) {
        this.id = id;
        this.placa = placa;
        this.capacidad = capacidad;
        this.modelo = modelo;
        this.marca = marca;
    }

    public Automovil() {

    }

    public int getId() {
         return id; 
     }

    public void setId(int id) {
         this.id = id; 
    }

    public String getPlaca(){
         return placa; 
    }

    public void setPlaca(String placa)  {
         this.placa = placa; 
    }

    public int getCapacidad() {
         return capacidad; 
    }

    public void setCapacidad(int capacidad){
         this.capacidad = capacidad; 
    }

    public int getModelo(){
         return modelo; 
    }

    public void setModelo(int modelo) {
         this.modelo = modelo; 
    }

    public String getMarca(){
         return marca; 
    }

    public void setMarca(String marca) {
         this.marca = marca; 
    }         
}