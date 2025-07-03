# 📱 Aplicación Android de Reconocimiento de Texto (OCR)

Este proyecto es una aplicación móvil desarrollada en **Java nativo con Android Studio**, cuyo propósito es utilizar la cámara del dispositivo para **reconocer texto en tiempo real (OCR)**, permitir su almacenamiento en un **historial de escaneos** y aplicar **filtros de texto** que extraen datos relevantes como correos electrónicos, números telefónicos o fechas.

---

## 🎯 Objetivos del Proyecto

- Implementar una app funcional que use la cámara del dispositivo.
- Aplicar tecnología OCR utilizando **Google ML Kit**.
- Guardar los textos escaneados en una base de datos local.
- Permitir que el usuario filtre y extraiga información relevante del texto.
- Presentar una interfaz sencilla y funcional.

---

## 🧩 Componentes que debe tener el proyecto

### 1. 📸 Reconocimiento de texto con la cámara (OCR)

- Integración con **CameraX API** para capturar imágenes en tiempo real.
- Uso de **ML Kit (Text Recognition)** para extraer texto directamente desde la imagen de la cámara.
- Procesamiento eficiente con `ImageAnalysis` e `InputImage`.

### 2. 🗃️ Historial de escaneos

- Almacenamiento local de los textos detectados utilizando **SQLite**.
- Guardado automático del texto y la fecha del escaneo.
- Visualización en pantalla mediante un `ListView` o `RecyclerView`.
- Posibilidad de eliminar entradas del historial si se desea.

### 3. 🔍 Filtros de texto

- Herramientas para detectar patrones específicos dentro del texto escaneado.
- Filtros disponibles (usando expresiones regulares en Java):
  - **Correos electrónicos**: ejemplo@dominio.com
  - **Números telefónicos**: +50760000000 o 60000000
  - **Fechas**: 02/07/2025
  - **Cédulas panameñas**: 8-888-8888
- Aplicación de filtros desde una clase utilitaria (`FiltroUtils.java`).

---

## 📱 Pantallas Esperadas

| Pantalla                 | Descripción                                            |
|--------------------------|--------------------------------------------------------|
| 🏠 **Pantalla principal** | Vista previa de la cámara + botón de escanear texto    |
| ✅ **Resultado OCR**      | Muestra el texto detectado + opciones para guardar     |
| 📜 **Historial**         | Lista de textos escaneados con fecha y hora            |
| 👤**pantalla de login**  | Login para ver registro de escaneos por usuarios       |
| 🔎 **Aplicar filtros**   | Botón para seleccionar tipo de filtro y ver resultados |

---

## 📦 Dependencias necesarias

En tu archivo `build.gradle (Module: app)`, asegúrate de tener:

gradle:
  implementation 'com.google.mlkit:text-recognition:16.0.0'
  implementation 'androidx.camera:camera-core:1.3.0'
  implementation 'androidx.camera:camera-camera2:1.3.0'
  implementation 'androidx.camera:camera-lifecycle:1.3.0'
  implementation 'androidx.camera:camera-view:1.3.0'
  
⚙️Permisos requeridos:
Agrega los siguientes permisos en AndroidManifest.xml:
xml:
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

Y dentro del <application>: android:requestLegacyExternalStorage="true"

## 🗃️ Base de datos local (SQLite)
🔐 Inicio de sesión de usuarios
Para permitir que cada usuario visualice únicamente su propio historial de escaneos, el proyecto debe incorporar una tabla de usuarios y relacionarla con los registros escaneados.

📋 Tabla: usuarios

CREATE TABLE usuarios (
id INTEGER PRIMARY KEY AUTOINCREMENT,
nombre_usuario TEXT NOT NULL UNIQUE,
contraseña TEXT NOT NULL
);
hashear contraseña obviamente

📋 Tabla: historial

CREATE TABLE historial (
id INTEGER PRIMARY KEY AUTOINCREMENT,
texto TEXT NOT NULL,
fecha TEXT NOT NULL,
id_usuario INTEGER NOT NULL,
FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

## 📦 Clases necesarias
Clase	Descripción
LoginActivity.java	Pantalla de inicio de sesión (usuario y contraseña)
RegisterActivity.java	Pantalla de registro para nuevos usuarios
DBHelper.java (extendida)	Incluye métodos para manejar login y registros de usuarios

## 🔑 Métodos que debe tener la clase DBHelper.java:

// Registro de nuevo usuario
public boolean registrarUsuario(String nombreUsuario, String contraseña);

// Validación de inicio de sesión
public boolean validarUsuario(String nombreUsuario, String contraseña);

// Obtener ID del usuario por nombre
public int obtenerIdUsuario(String nombreUsuario);

// Insertar historial asociado al usuario
public boolean insertarTexto(String texto, String fecha, int idUsuario);

// Obtener historial por usuario
public Cursor obtenerHistorialPorUsuario(int idUsuario);

## Flujo de app
1. El usuario abre la app y se presenta la pantalla de login.

2. Si es nuevo, puede registrarse con un nombre de usuario y contraseña.

3. Luego de iniciar sesión, se accede a la cámara y OCR.

4. Los textos escaneados se guardan con el id_usuario.

5. El historial muestra solo los escaneos del usuario que inició sesión.


## 👥 División de tareas entre los 4 integrantes del proyecto OCR
responsabilidades de cada uno:

## 🧑‍💼 Integrante 1: Login + Base de Datos:Crear el login y base de datos.
Responsabilidades:

- Crear LoginActivity.java y RegisterActivity.java.

- Diseñar los layouts correspondientes (activity_login.xml, activity_register.xml).

- Desarrollar la clase DBHelper.java que manejará:

  - Registro e inicio de sesión de usuarios.
  
  - Inserción y consulta del historial de escaneos.

- Debe proveer al equipo métodos reutilizables como:

   - boolean registrarUsuario(nombre, contraseña)
  
   - boolean validarUsuario(nombre, contraseña)
  
   - boolean insertarTexto(String texto, String fecha, int usuarioId)
  
   - List<String> obtenerHistorialPorUsuario(int usuarioId)

## 📜 Integrante 2: Registro + Historial:Crear el layout de registrar usuario (usando los metodos dados por el encargado de la base de datos+login) y el layout del historial.
Responsabilidades:

- Crear una clase intermedia o servicio que reciba texto desde la lógica OCR y lo guarde en la base de datos usando métodos del DBHelper.

- Implementar HistorialActivity.java que muestre todos los textos escaneados por el usuario actual.

- Crear el diseño activity_historial.xml y un item_historial.xml para el RecyclerView.

- Debe proveer una función como:

  - guardarTextoEscaneado(String texto)

- El de la lógica OCR solo deberá invocar esta función para guardar.

## 📷 Integrante 3: Lógica de Cámara + OCR (Activity principal):Logica de escanear imagen y trasnformar a texto y layout de la pagina principal.
Responsabilidades:

- Configurar CameraX para obtener imágenes en tiempo real.

- Integrar ML Kit para realizar OCR sobre cada imagen capturada.

- Crear MainActivity.java y su layout (activity_main.xml) con vista previa de cámara y botón de escanear.

- Mostrar el texto detectado en pantalla y enviarlo al responsable del historial mediante:

  - guardarTextoEscaneado(textoReconocido)

- No debe encargarse del guardado directo: solo escanea, convierte a texto y delega.

## 🔍 Integrante 4: Filtros y patrones de texto:El encargado de la logica Ocr luego de escanear mantara el texto a Activity donde podra aplicar los filtros y se podra ver una lsita de lo encontrado si presioan fecha pues uan lista de fechas etc..
Responsabilidades:

- Crear FiltroUtils.java con métodos estáticos que usen expresiones regulares para extraer:

  - Correos electrónicos
  
  - Números telefónicos
  
  - Fechas
  
  - Cédulas panameñas

- Implementar FiltrosActivity.java que permita al usuario seleccionar qué tipo de patrón quiere aplicar al texto ya escaneado.

- Crear el diseño activity_filtros.xml y vista de resultados.

- Métodos esperados en FiltroUtils:

  - List<String> extraerCorreos(String texto)
  
  - List<String> extraerFechas(String texto)
  
  - List<String> extraerTelefonos(String texto)
  
  - List<String> extraerCedulas(String texto)

