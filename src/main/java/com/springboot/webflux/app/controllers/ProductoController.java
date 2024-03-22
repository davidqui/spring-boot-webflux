package com.springboot.webflux.app.controllers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

import com.springboot.webflux.app.models.services.ProductoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SessionAttributes("producto")
@Controller
public class ProductoController {

    @Autowired
    private ProductoService service;

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @GetMapping({"/listar", "/"})
    public Mono<String> listar(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCase();

        productos.subscribe(prod -> log.info(prod.getNombre()));

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return Mono.just("listar");
    }

    @GetMapping("/form")
    public Mono<String> crear(Model model) {

        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Formulario de productos");
        model.addAttribute("boton", "Crear");
        return Mono.just("form");
    }

    /**
     * Meto de editar productos de una forma mas reactiva
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/form-v2/{id}")
    public Mono<String> editarV2(@PathVariable String id, Model model) {
        return service.findById(id).doOnNext(p -> {
                    log.info("Producto: " + p.getNombre());
                    model.addAttribute("boton", "Editar");
                    model.addAttribute("titulo", "Editar productos");
                    model.addAttribute("producto", p);
                }).defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("No existe el producto"));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));

    }


    /**
     * Meto de editar productos
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/form/{id}")
    public Mono<String> editar(@PathVariable String id, Model model) {
        Mono<Producto> productoMono = service.findById(id).doOnNext(p -> {
            log.info("Producto: " + p.getNombre().toUpperCase());
        }).defaultIfEmpty(new Producto());
        return productoMono.flatMap(producto -> {

            model.addAttribute("titulo", "Editar productos");
            model.addAttribute("boton", "Editar");
            model.addAttribute("producto", producto);

            return Mono.just("form");
        });
    }

    /**
     * Metodo que guarda el producto
     *
     * @param producto
     * @return
     */
    @PostMapping("/form")
    public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Errores en el formulario de producto");
            model.addAttribute("boton", "Guardar");
            return Mono.just("form");
        } else {
            status.setComplete();// indica que finalizó la sesion
            if (producto.getCreateAt() == null) {
                producto.setCreateAt(LocalDate.now());
            }
            return service.save(producto).doOnNext(p -> {
                log.info("Producto guardado: " + p.getNombre() + p.getId());
            }).thenReturn("redirect:/listar?success=producto+guardado+con+exito");
        }

    }

    @GetMapping("/listar-datadriver")
    public String listarDataDriver(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCase().delayElements(Duration.ofSeconds(1));

        productos.subscribe(prod -> log.info(prod.getNombre()));

        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 1));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    /**
     * @param model
     * @return
     */
    @GetMapping("/listar-full")
    public String listarFull(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCaseRepeat();

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCaseRepeat();

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar-chunked";
    }
}
