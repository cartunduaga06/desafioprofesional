Infraestructura local – Sprint 4 (Digital Money House)

Este repositorio proporciona la infraestructura necesaria para arrancar el backend del proyecto Digital Money House en el Sprint 4, junto con una base de datos MySQL utilizando Docker Compose. El objetivo es que cualquier miembro del equipo —especialmente QA— pueda levantar el stack completo de forma reproducible y sin ajustes manuales y probar las nuevas funcionalidades de transferencias entre cuentas, además de las operaciones de actividad, dashboard, tarjetas y saldo.

Estructura del repositorio
.
├── backend/            # Código fuente Java + Dockerfile multi‑etapa
│   ├── pom.xml
│   ├── src/
│   └── Dockerfile
├── docker-compose.yml  # Define los servicios db y backend
├── .env.example        # Plantilla de variables de entorno (no incluye secretos)
└── README.md           # Guía de infraestructura (este documento)


La carpeta backend alberga el microservicio Spring Boot corregido (Jakarta EE, JWT HS256). Contiene un Dockerfile multi‑etapa que compila el proyecto con Maven y empaqueta el jar en una imagen ligera de JRE.

Variables de entorno

Todas las credenciales y parámetros de configuración se cargan desde un archivo .env. No se debe commitear .env al repositorio; utiliza el archivo .env.example como plantilla. A continuación se describen las variables disponibles:

Categoría	Variable	Descripción	Valor por defecto
App	SPRING_PROFILES_ACTIVE	Perfil de Spring activo (p.ej. prod)	prod
	TZ	Zona horaria del contenedor	America/Bogota
JWT	JWT_SECRET	Secreto utilizado para firmar los tokens HS256	REEMPLAZAR…
	JWT_EXPIRATION_MS	Tiempo de expiración del token en milisegundos	86400000
Base de datos	MYSQL_HOST	Hostname del servicio MySQL dentro de la red Docker	db
	MYSQL_PORT	Puerto en el que escucha MySQL	3306
	MYSQL_DATABASE	Nombre de la base de datos a crear	dmh
	MYSQL_USER	Usuario de aplicación	dmh_user
	MYSQL_PASSWORD	Contraseña del usuario de aplicación	dmh_pass
	MYSQL_ROOT_PASSWORD	Contraseña del usuario root
docs.docker.com
	dmh_root
Puertos	APP_PORT	Puerto que expone el microservicio	8080

Importante: las variables prefijadas con MYSQL_ son reconocidas por la imagen oficial de MySQL y permiten inicializar el servidor creando la base de datos, el usuario y asignando contraseñas
docs.docker.com
.

Cómo levantar el stack con Docker

Preparación. Clona este repositorio y ubícate en la raíz:

git clone <url-del-repositorio>
cd digital-money-house


Configura tus variables. Copia el archivo de ejemplo y edita las variables según tus necesidades:

cp .env.example .env
# Edita .env y reemplaza JWT_SECRET por un valor seguro y ajusta credenciales si procede


Construye las imágenes. Ejecuta la compilación desde la raíz del proyecto para preparar las imágenes de db y backend:

docker compose build


La imagen backend se genera mediante un proceso multi‑etapa que compila el jar usando Maven y lo copia a una imagen de ejecución. La imagen db utiliza mysql:8.0 y crea automáticamente la base de datos y el usuario indicados.

Arranca los contenedores. Levanta todo el stack en modo desacoplado:

docker compose up -d


Docker iniciará primero el servicio de base de datos y, gracias a la directiva depends_on con condición de salud, el backend esperará hasta que MySQL esté listo antes de iniciar
docs.docker.com
.

Verifica el estado. Usa el comando docker compose ps para confirmar que ambos servicios están ejecutándose y observa los puertos mapeados:

docker compose ps


Deberías ver el servicio db escuchando en 0.0.0.0:${MYSQL_PORT}->3306/tcp y el servicio backend en 0.0.0.0:${APP_PORT}->8080/tcp.

Accede al microservicio. Una vez que el stack esté en ejecución:

API base: http://localhost:${APP_PORT}/

Endpoint de salud (si está habilitado Actuator): http://localhost:${APP_PORT}/actuator/health

Swagger UI: http://localhost:${APP_PORT}/swagger-ui.html

Conéctate a la base de datos. Puedes acceder a MySQL desde tu host con el cliente de línea de comandos o una herramienta gráfica:

mysql -h 127.0.0.1 -P ${MYSQL_PORT} -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE}


El mapeo de puertos en el docker-compose.yml permite conectarse desde el host y las variables de entorno de la imagen MySQL crean la base de datos y el usuario
docs.docker.com
.

Parada y limpieza. Para detener los servicios sin perder datos utiliza:

docker compose down


Si deseas eliminar también el volumen de datos (db_data), ejecuta:

docker compose down -v

Actualización y reconstrucción del backend

Durante el desarrollo es posible que necesites reconstruir únicamente el microservicio sin afectar la base de datos. Después de modificar el código fuente en backend, ejecuta:

docker compose build backend
docker compose up -d backend


Esto volverá a compilar la imagen de backend y reiniciará el servicio, manteniendo el estado de la base de datos.

Pruebas de QA para Sprint 4

En este sprint se incorpora la transferencia de dinero entre cuentas, al tiempo que se mantienen todas las funcionalidades anteriores: dashboard, filtros de actividad, ingresos, gestión de tarjetas y consulta de saldo y movimientos. La siguiente guía describe cómo probar estos endpoints usando Swagger una vez que el stack esté en ejecución:

Registrar y autenticar un usuario. Utiliza los endpoints existentes (POST /users/register y POST /auth/login) para crear un usuario y obtener un token JWT. Desde Swagger UI puedes ejecutar estas operaciones y copiar el token devuelto en la respuesta de login.

Autorizar en Swagger. En la interfaz de Swagger (/swagger-ui.html) haz clic en el botón Authorize, introduce el token en el campo Bearer <token> (incluye el prefijo Bearer seguido de un espacio) y confirma. Esto añadirá el token a las peticiones protegidas.

Consultar datos de la cuenta. En el dashboard se expone un endpoint tipo GET /accounts/{accountId} que devuelve la información principal de la cuenta (CVU, alias y saldo disponible). Proporciónale el accountId que corresponda al usuario autenticado.

Consultar últimos movimientos. Localiza el endpoint GET /accounts/{accountId}/transactions para recuperar las últimas cinco transacciones asociadas a la cuenta. Verifica que se devuelve una lista ordenada de más reciente a más antigua.

Listar y filtrar la actividad completa. El endpoint GET /accounts/{accountId}/activity permite consultar todo el historial de transacciones y admite filtros opcionales por monto mínimo y máximo (minAmount, maxAmount), rango de fechas (startDate, endDate) o tipo de operación (type). Prueba enviar distintas combinaciones de parámetros de consulta y confirma que los resultados se filtran correctamente.

Consultar detalle de una transacción. Para obtener el detalle de una transacción específica, usa el endpoint GET /accounts/{accountId}/activity/{transactionId} con el identificador de la transacción devuelto por los listados anteriores.

Actualizar alias de la cuenta. El dashboard también ofrece un endpoint PATCH /accounts/{accountId} que permite actualizar el alias de la cuenta enviando un AccountUpdateRequest en el cuerpo. Comprueba que el alias se actualiza y que se manejan los errores si el alias ya está en uso.

Registrar ingresos (transferencias entrantes). Para registrar un ingreso desde una tarjeta a la cuenta, llama al endpoint POST /accounts/{accountId}/transferences enviando un TransferenceRequest con los campos requeridos (por ejemplo cardId, amount y description). Valida que se cree una nueva transacción con tipo INCOME y que el saldo de la cuenta aumenta.

Transferencias de dinero. Para realizar una transferencia a otra cuenta utiliza el endpoint POST /accounts/{accountId}/transferences. El cuerpo de la solicitud debe incluir los datos requeridos por TransferRequest (por ejemplo, cvuDestino, aliasDestino o accountIdDestino, monto y concepto). Asegúrate de que el usuario autenticado coincide con el accountId de origen; de lo contrario, la API devolverá un error 403.

Últimos destinatarios. El endpoint GET /accounts/{accountId}/transferences devuelve una lista de los destinatarios más recientes a los que se ha transferido dinero. Comprueba que se devuelven correctamente y que están asociados con el usuario autenticado.

Operaciones de tarjetas. Las operaciones con tarjetas (alta de tarjeta, listado y eliminación) se mantienen bajo el recurso /accounts/{accountId}/cards. Usa los endpoints GET, POST y DELETE para listar, crear y eliminar tarjetas respectivamente, proporcionando la información necesaria en cada caso.

Verificar respuestas y errores. Para cada endpoint, asegúrate de que se devuelve una respuesta HTTP adecuada y que los datos coinciden con las operaciones realizadas. Revisa también que se manejen correctamente los errores (por ejemplo, peticiones sin token, parámetros inválidos, saldo insuficiente o tarjetas inexistentes).

Aun cuando los nombres exactos de los endpoints pueden variar según la implementación, la documentación generada automáticamente en Swagger UI mostrará el listado completo con descripciones y parámetros de entrada. Utiliza esa referencia como guía principal durante las pruebas de QA.

Troubleshooting (solución de problemas)

Puertos ocupados. Si los puertos 3306 o 8080 ya están en uso en tu máquina, modifica MYSQL_PORT y/o APP_PORT en tu .env antes de ejecutar docker compose up.

Errores de conexión a la base de datos. La imagen de MySQL tarda algunos segundos en inicializarse. El microservicio espera a que la base de datos esté healthy gracias al healthcheck y a depends_on
docs.docker.com
. Si aun así ves errores de conexión en los logs, vuelve a ejecutar docker compose up -d después de unos segundos o verifica que MYSQL_HOST, MYSQL_DATABASE, MYSQL_USER y MYSQL_PASSWORD estén configurados correctamente.

Permisos en volúmenes. Si MySQL no arranca debido a permisos en el directorio de datos, elimina el volumen con docker compose down -v y vuelve a levantar el entorno. También puedes dar permisos adecuados al directorio de volúmenes en tu sistema operativo.

Nombre de artefacto diferente. El Dockerfile copia dmh-app*.jar. Si cambias el nombre del artefacto en el pom.xml (etiqueta <finalName>), ajusta la ruta en la instrucción COPY --from=build del Dockerfile para que coincida con el nombre del jar generado.

Variables de entorno no leídas. Asegúrate de que application.properties o application.yml utilicen las variables de entorno definidas en .env para configurar la conexión JDBC y otros parámetros. Las propiedades de Spring Boot se pueden enlazar mediante la sintaxis ${VARIABLE:valorPorDefecto}.

Con esta guía podrás levantar y mantener la infraestructura del backend de Digital Money House de manera fiable y reproducible. Ajusta las configuraciones según las necesidades de tu entorno y asegúrate de no exponer secretos en los archivos versionados.

Despliegue y consideraciones de producción

Además del entorno local, Sprint 4 contempla el despliegue del frontend en Vercel, la posibilidad de almacenar imágenes (por ejemplo, fotos de tarjetas o comprobantes) en Amazon S3, y la preparación de una imagen Docker del backend lista para subir a un registro (por ejemplo AWS ECR).

Frontend en Vercel

El frontend (no incluido en este repositorio) se despliega automáticamente en Vercel. Para que la aplicación web pueda consumir la API del backend debes asegurarte de lo siguiente:

El backend está accesible a través de una URL pública o un túnel seguro (p. ej., usando un servicio como ngrok
 durante el desarrollo). Configura la variable de entorno API_BASE_URL en el proyecto frontend para apuntar a la dirección y puerto donde se ejecuta este backend.

Si implementas CORS en Spring Boot, añade el dominio de Vercel (*.vercel.app) a la lista de orígenes permitidos. Esto se suele configurar en una clase WebSecurityConfig o mediante propiedades spring.web.cors.

Almacenamiento en S3 (opcional)

Si el backend incorpora la carga de imágenes de tarjetas u otras actividades en un bucket de Amazon S3, deberás:

Crear un bucket en AWS y definir su región. Toma nota de su nombre (AWS_BUCKET) y de la región (AWS_REGION).

Generar claves de acceso (Access Key ID y Secret Access Key) con permisos mínimos sobre ese bucket.

Definir las variables en tu .env y pasarlas al contenedor backend (como se muestra en docker-compose.yml):

AWS_REGION=us-east-1
AWS_BUCKET=mi-bucket-dmh
AWS_ACCESS_KEY_ID=<tu-access-key-id>
AWS_SECRET_ACCESS_KEY=<tu-secret-key>


Asegurarte de que el código Java utilice estas variables de entorno para inicializar el cliente de S3.

Estas variables se declaran en el docker-compose.yml y en .env.example para que puedas configurarlas según tu entorno. Si no usas S3, puedes dejarlas vacías o eliminar su definición.

Construcción y despliegue de la imagen Docker (ECR)

Para desplegar el backend en la nube se recomienda construir una imagen Docker a partir del Dockerfile multi‑etapa y subirla a un registro como AWS Elastic Container Registry (ECR). A continuación se muestra un flujo típico utilizando AWS CLI:

Iniciar sesión en ECR (una sola vez por región/credenciales). Sustituye <aws_account_id> y <region> por los valores de tu cuenta.

aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <aws_account_id>.dkr.ecr.<region>.amazonaws.com


Crear el repositorio (si no existe):

aws ecr create-repository --repository-name dmh-backend --region <region>


Construir la imagen desde la carpeta backend y etiquetarla con la URI del repositorio:

# desde la raíz del proyecto
docker build -t dmh-backend:latest -f backend/Dockerfile ./backend
docker tag dmh-backend:latest <aws_account_id>.dkr.ecr.<region>.amazonaws.com/dmh-backend:latest


Subir la imagen al repositorio:

docker push <aws_account_id>.dkr.ecr.<region>.amazonaws.com/dmh-backend:latest


Una vez publicada la imagen en ECR, podrás utilizarla en los servicios de AWS (por ejemplo, Elastic Container Service, Lambda con contenedores o Kubernetes). Para despliegues en otros proveedores, adapta los comandos de docker login, tag y push al registro correspondiente.

CI/CD y pruebas automatizadas (opcional)

Para una integración continua, se recomienda configurar un pipeline que ejecute las pruebas unitarias y de integración (por ejemplo, con JUnit y RestAssured), construya la imagen del backend y despliegue en el ambiente deseado. Herramientas como GitHub Actions, GitLab CI o Jenkins son adecuadas para automatizar estas tareas.

Un ejemplo sencillo de pipeline podría incluir los siguientes pasos:

checkout del código.

Configuración de JDK 17 y Maven.

Ejecución de pruebas con mvn test.

Construcción de la imagen Docker con docker build utilizando el Dockerfile multi‑etapa.

Publicación de la imagen en un registro (Docker Hub, GitHub Packages, AWS ECR, etc.).

Despliegue en un servicio de hosting o en un clúster (p. ej. AWS ECS, Kubernetes o un VPS) y actualización de la URL base que consume el frontend.

Esta sección es opcional, pero aporta transparencia y agilidad a la entrega continua de nuevas funcionalidades.