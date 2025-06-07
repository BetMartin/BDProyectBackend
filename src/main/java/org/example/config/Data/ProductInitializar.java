package org.example.config.Data;
import org.example.entity.Product;
import org.example.repository.ProductRepository;
import org.example.repository.ProductCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class ProductInitializar implements CommandLineRunner{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private final Logger logger = LoggerFactory.getLogger(ProductInitializar.class);

    @Override
    public void run(String... args) {
        try {
            if (productRepository.count() == 0) {
                logger.info("Iniciando carga de productos...");

                Object[][] productos = {
                        {"Nike", "Impulsa tu estilo a toda velocidad con el Air Max 97. El diseño icónico está inspirado en las gotas de agua y los trenes bala japoneses. La amortiguación Nike Air de largo completo te permite andar con comodidad de alto rendimiento. Amárrate las agujetas", "nike.png", "AirMax", "Nike Aire Maz", 1L},
                        {"Adidas", "Los Adidas Crazyfast TF Red JR son la elección perfecta para jóvenes futbolistas que buscan rendimiento y estilo en el campo. Diseñados para proporcionar un ajuste cómodo y un excelente soporte, estos botines destacan por su color rojo vibrante que garant", "adidas.png", "CrazyFast", "Adidas Crazy Fast", 2L},
                        {"Puma", "Conocé las nuevas zapatillas unisex de la familia X-Ray, que te ofrecen lo último en estilo urbano y comodidad duradera. Cuentan con una liviana y moldeada entresuela de IMEVA, una suela de goma de buen agarre y una llamativa capellada con detalles de col", "puma.png", "X-Ray-3", "Puma X-Ray", 3L},
                        {"Wilson", "Da un paso en cada golpe con confianza y comodidad cuando te atas las Kaos Stroke 2.0, unas zapatillas con amortiguación amplificada en la entresuela para una sensación más suave en la cancha. Uno de los zapatos más duraderos de la línea Kaos orientada a", "wilson.png", "Kaos-Stroke-2", "Wilson Kaos-Stroke", 4L},
                        {"Nike Jordan", "La gamuza premium y la espuma Formula 23 exclusiva de la marca Jordan se unen para brindarte un AJ1 mucho más lujoso (y cómodo). Con este calzado no necesitas elegir entre estilo o comodidad, lo cual está bien porque te mereces las dos cosas.", "nikejordan.png", "Tenis Air Jordan", "Nike Jordan Air1- Retro", 1L},
                        {"Nike", "Los Nike Charge son un básico del skateboarding. Con un diseño de perfil bajo, combinan gamuza suave y una suela flexible para brindar comodidad desde el primer uso.", "fc9b1460-52e0-40d7-a747-8a3f096ae3f2.png", "Charge Suede", "Zapatillas Nike Charge", 3L},
                        {"Reebok", "Conoce la nueva forma de correr, el FloatZig 1 de alto retorno de energía. Diseñado para inspirar a todos a atarse los cordones y salir al aire libre, estas zapatillas para correr están fabricadas con tecnología de espuma ligera de primera calidad.", "533f77f8-909d-4c19-8ae7-e6a36dd0ab74.png", "Floatzig1", "Reebok Floatzig", 3L},
                        {"Puma", "Usadas por la estrella de la NBA Scoot Henderson, estas zapatillas unisex PUMA Basketball fusionan cultura automovilística y destreza atlética", "ee6e0ae3-8fc3-4031-a55d-833db37bab8c.png", "Scoot Zeros II Caution", "Básquet Scoot Zeros II", 5L}
                };

                for (Object[] prod : productos) {
                    Product product = new Product();
                    product.setBrand((String) prod[0]);
                    product.setDescription((String) prod[1]);
                    product.setImage((String) prod[2]);
                    product.setModel((String) prod[3]);
                    product.setProduct((String) prod[4]);

                    // Buscar y establecer la categoría
                    productCategoryRepository.findById((Long) prod[5])
                            .ifPresent(product::setProductCategory);

                    productRepository.save(product);
                }

                logger.info("Productos cargados exitosamente");
            } else {
                logger.info("La tabla de productos ya contiene datos, saltando inicialización");
            }
        } catch (Exception e) {
            logger.error("Error al inicializar productos: " + e.getMessage());
        }
    }
}
