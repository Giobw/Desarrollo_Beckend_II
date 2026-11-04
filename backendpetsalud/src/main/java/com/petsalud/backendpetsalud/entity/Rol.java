package com.petsalud.backendpetsalud.entity;

/**
 * Roles del sistema segun el caso: cliente, empleado y gerente.
 * Spring Security los expone como ROLE_CLIENTE, ROLE_EMPLEADO y ROLE_GERENTE.
 */
public enum Rol {
    CLIENTE,
    EMPLEADO,
    GERENTE
}
