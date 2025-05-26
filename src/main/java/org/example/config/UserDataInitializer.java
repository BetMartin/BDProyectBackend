package org.example.config;

import org.example.entity.*;
import org.example.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)

public class UserDataInitializer implements CommandLineRunner{

    private final ProvinceRepository provinceRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final Logger logger = LoggerFactory.getLogger(UserDataInitializer.class);


    @Autowired
    public UserDataInitializer(AddressRepository addressRepository, PhoneRepository phoneRepository, UserRepository userRepository, PersonRepository personRepository, ProvinceRepository provinceRepository) {
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.provinceRepository = provinceRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (provinceRepository.count() == 0) {
                logger.info("Iniciando carga de provincias...");
                String[] provincias = {"Buenos Aires", "Córdoba", "Santa Fe", "Mendoza", "Salta"};

                for (String nombreProvincia : provincias) {
                    Province province = new Province();
                    province.setName(nombreProvincia);
                    provinceRepository.save(province);
                }
                logger.info("Provincias cargadas exitosamente");
            }
        } catch (Exception e) {
            logger.error("Error al inicializar provincias: " + e.getMessage());
        }
        try {
            if (phoneRepository.count() == 0) {
                logger.info("Iniciando carga de teléfonos...");
                String[] telefonos = {"3512345678", "3812345678", "3412345678", "2612345678", "3871234567"};

                for (String numero : telefonos) {
                    Phone phone = new Phone();
                    phone.setNumber(numero);
                    phoneRepository.save(phone);
                }
                logger.info("Teléfonos cargados exitosamente");
            }

            if (addressRepository.count() == 0) {
                logger.info("Iniciando carga de direcciones...");
                String[][] direcciones = {
                        {"A", "Av. Siempre Viva", "742", "1"},
                        {"B", "Calle Falsa", "123", "2"},
                        {"sinDpto", "San Martín", "456", "3"},
                        {"C", "Belgrano", "789", "4"},
                        {"sinDpto", "Mitre", "101", "5"}
                };

                for (String[] dir : direcciones) {
                    Address address = new Address();
                    address.setApartment(dir[0]);
                    address.setStreet(dir[1]);
                    address.setStreetNumber(Integer.parseInt(dir[2]));
                    provinceRepository.findById(Long.parseLong(dir[3]))
                            .ifPresent(address::setProvince);
                    addressRepository.save(address);
                }
                logger.info("Direcciones cargadas exitosamente");
            }
        } catch (Exception e) {
            logger.error("Error en la inicialización: " + e.getMessage());
        }
        try {
            if (userRepository.count() == 0) {
                logger.info("Iniciando carga de usuarios y personas...");

                // Primero creamos los usuarios
                Object[][] usuarios = {
                        {"admin1", "pass1"},
                        {"user2", "pass2"},
                        {"admin2", "pass3"},
                        {"user4", "pass4"},
                        {"user5", "pass5"}
                };

                Object[][] personas = {
                        {"Juan", "Pérez", "ADMIN", 1L, 1L, 1L, "45869751"},
                        {"Ana", "Gómez", "VISOR", 2L, 2L, 2L, "41587965"},
                        {"Luis", "Martínez", "ADMIN", 3L, 3L, 3L, "37895423"},
                        {"María", "Fernández", "VISOR", 4L, 4L, 4L, "445879451"},
                        {"Pedro", "López", "VISOR", 5L, 5L, 5L, "45879632"}
                };

                for (int i = 0; i < usuarios.length; i++) {
                    User user = new User();
                    user.setUsername((String)usuarios[i][0]);
                    // Encriptamos la contraseña usando el método de la entidad User
                    String passwordEncriptada = user.encriptarClave((String)usuarios[i][1]);
                    user.setPassword(passwordEncriptada);
                    user = userRepository.save(user);

                    Person person = new Person();
                    person.setFirstName((String)personas[i][0]);
                    person.setLastName((String)personas[i][1]);
                    person.setRol(Rol.valueOf((String)personas[i][2]));

                    addressRepository.findById((Long)personas[i][3])
                            .ifPresent(person::setAddress);
                    phoneRepository.findById((Long)personas[i][4])
                            .ifPresent(person::setPhone);
                    person.setUser(user);
                    person.setDni(Integer.parseInt((String)personas[i][6]));

                    personRepository.save(person);
                }

                logger.info("Usuarios y personas cargados exitosamente");
            }
        } catch (Exception e) {
            logger.error("Error en la inicialización: " + e.getMessage());
        }




    }

}
