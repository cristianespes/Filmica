# FILMICA

Se han añadido los siguientes puntos a la aplicación inicial para completar la App.


## Trending

Historia de usuario:

Los usuarios de Fílmica quieren saber cuál el trending semanal de peliculas, así que con ayuda de The Movie DB se va a construir una sección de Trends donde se pueda visualizar este listado.

Detalles de implementación:

- Se va a añadir una opción más a nuestro ​BottomNavigationView.​
- La sección de trending movies mostrará un listado de Films.
- La UI de los items de este listado será la misma que la de la sección de D​iscover​.


## Búsqueda de Films

Historia de usuario:

A los usuarios les gustaría buscar información sobre películas que les han recomendado, para esto se va a añadir una nueva sección de nuestra aplicación para poder realizar búsquedas.

Detalles de implementación:

- Se añadirá una opción más al ​BottomNavigationView.​
- La sección tendrá un​ EditText p​ara poder realizar búsquedas por nombre.
- Al no haber resultados de búsqueda, la pantalla mostrará un mensaje en la pantalla, parecido al implementado cuando hubo un error en la petición de Discover
- Se añade una ProgressBar para indicarle al usuario que se está llevando a cabo la búsqueda
- La lógica de búsqueda será la siguiente: el usuario ingresará un query de mínimo 3 caracteres, al ingresarlo se realizará una búsqueda y a lo más se mostrarán 10 resultados.


## Detalle de Trending Films, Watchlist y Resultados de búsqueda

Historia de usuario:

Los usuarios quieren poder acceder al detalle de las películas en las nuevas secciones de la aplicación. Al seleccionar un item mostrará el detalle de la película.


## Undo de salvar film y eliminar film

Historia de usuario:

Algunos usuarios han apretado el botón de agregar película y han eliminado películas de su watchlist por error, es por eso que se integra un mecanismo para realizar un 'undo​' de estas acciones.

Detalles de implementación:

- Se hace uso del componente 'S​nackba​r​' incluido en la biblioteca de design.


## Placeholder de sección de detalle en tablets, watchlist y trends

Historia de usuario:

Al ejecutar la aplicación en modo landscape para tablets es un poco extraño tener un gran espacio en blanco vacío sin ninguna información. Se añade un placeholder parecido a una marca de agua con el logotipo de Fílmica para indicarle al usuario que en ese espacio se podrán visualizar los detalles de las películas que él elija.


## Paginación de Trends y Discover

Historia de usuario:

Al usuario le gustaría poder acceder a más películas ya que actualmente los listados muestran únicamente cerca de 10 items.

Detalles de implementación:

- Se implementa un mecanismo de scroll infinito al ​RecyclerView​, de manera que al momento en que el usuario haga un scroll hasta el último item, la aplicación realice un request para obtener la siguiente página de items.
- Para realizar esta funcionalidad se opta por crear un comportamiento a partir de un **RecyclerView.OnScrollListener**.