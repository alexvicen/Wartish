# Guía visual de Wartish

Aplica esta dirección visual a toda interfaz nueva o modificada del proyecto.

## Identidad

- Wartish es una aventura de fantasía medieval con arte pixelado y materiales rústicos.
- La madera oscura, la piedra, el hierro envejecido, el cuero, el pergamino y el latón son los materiales visuales principales.
- Usa una paleta cálida de marrones, ocres, carbón y crema; reserva los tonos de brasa para la forja y los acentos dorados para acciones importantes.
- Conserva los sprites existentes y escálalos sin suavizado (`FilterQuality.None`). Prefiere los recursos de imagen del proyecto a emojis para representar objetos del juego.

## Componentes e interacción

- Construye paneles como tablones, placas, cofres, pergaminos o piezas de piedra con bordes visibles y contraste suficiente.
- Los botones deben parecer placas o piezas forjadas y tener etiquetas breves, legibles y en español.
- Los elementos seleccionables deben mostrar su nombre, nivel y estado disponible cuando quepan en el diseño. Mantén áreas táctiles amplias.
- Las mejoras de equipo muestran el coste y el efecto antes de confirmar; deshabilita la acción cuando falten recursos.
- Los diálogos de contexto del juego pueden abrirse como paneles inferiores con el mismo lenguaje material y una salida clara.
- Mantén la información y controles importantes visibles en la orientación vertical actual y adapta el contenido a distintos tamaños sin solapamientos.

## Implementación

- Usa componentes Compose reutilizables para superficies, controles y placas rústicas; evita introducir estilos visuales incompatibles en cada pantalla.
- No sustituyas los recursos pixel art por emojis, tarjetas blancas brillantes o estética tecnológica moderna.
- Conserva las convenciones de arquitectura y persistencia del proyecto. Al cambiar una interfaz, compila el proyecto y revisa errores; no hace falta iniciar pruebas visuales salvo que el usuario las pida.
