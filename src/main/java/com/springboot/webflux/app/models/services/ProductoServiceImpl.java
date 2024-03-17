package com.springboot.webflux.app.models.services;

import com.springboot.webflux.app.models.dao.ProductoDao;
import com.springboot.webflux.app.models.documents.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService{
    @Autowired
    private ProductoDao dao;


    /**
     *
     * @return
     */
    @Override
    public Flux<Producto> findAll() {
        return dao.findAll();
    }

    /**
     * @return
     */
    @Override
    public Flux<Producto> findAllConNombreUpperCase() {
        return dao.findAll().map(producto -> {

            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    /**
     * @return
     */
    @Override
    public Flux<Producto> findAllConNombreUpperCaseRepeat() {
        return findAllConNombreUpperCase().repeat(5000);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Mono<Producto> findById(String id) {
        return dao.findById(id);
    }

    /**
     * @param producto
     * @return
     */
    @Override
    public Mono<Producto> save(Producto producto) {
        return dao.save(producto);
    }

    /**
     * @param producto
     * @return
     */
    @Override
    public Mono<Void> delete(Producto producto) {
        return dao.delete(producto);
    }
}
