# 📱 Aplicación Android de Reconocimiento de Texto (OCR)
Proyecto final — Desarrollo de Aplicaciones Móviles (Android)

## 📝 Descripción General
Esta aplicación móvil, desarrollada en **Java nativo con Android Studio**, permite **reconocer texto mediante OCR en tiempo real** utilizando la cámara, almacenar los resultados en un **historial por usuario** y aplicar **filtros automáticos** para extraer información relevante como correos, teléfonos, fechas y cédulas panameñas.

El sistema incluye **inicio de sesión**, de modo que cada usuario cuenta con su propio historial privado.

## 🎯 Objetivos del Proyecto
- Integrar OCR en tiempo real con **CameraX** y **Google ML Kit**.  
- Guardar cada escaneo en una base de datos **SQLite** asociada al usuario.  
- Mostrar un **historial filtrado por usuario** con todos sus textos detectados.  
- Implementar **filtros inteligentes** basados en expresiones regulares.  
- Diseñar una interfaz intuitiva y funcional.

## 🧩 Componentes Principales

### 📸 1. OCR con cámara (Vista en tiempo real)
- Visualización en vivo mediante **CameraX**.  
- Captura mediante `ImageAnalysis`.  
- Reconocimiento de texto con **ML Kit Text Recognition**.  
- Conversión inmediata de imagen → texto.

### 🗃️ 2. Historial de escaneos por usuario
- Guardado en **SQLite**, enlazado al usuario activo.  
- Registro de texto y fecha.  
- Visualización con `RecyclerView`.  
- Acceso fácil y organizado al historial.

### 🔍 3. Filtros Inteligentes
El usuario puede aplicar filtros para extraer automáticamente:
- 📧 Correos electrónicos  
- 📞 Teléfonos  
- 📅 Fechas  
- 🆔 Cédulas panameñas  

Todo mediante expresiones regulares en `FiltroUtils.java`.

## 📱 Pantallas Principales

| Pantalla | Función |
|---------|---------|
| 🧑‍💼 Login / Registro | Gestión de usuarios |
| 🏠 Principal (OCR) | Cámara + botón de escanear |
| 📄 Resultado OCR | Muestra el texto detectado |
| 📜 Historial | Lista de textos guardados |
| 🔎 Filtros | Aplicación de patrones sobre el texto |

## 📦 Dependencias

Agregar en `build.gradle (Module: app)`:

```
implementation 'com.google.mlkit:text-recognition:16.0.0'
implementation 'androidx.camera:camera-core:1.3.0'
implementation 'androidx.camera:camera-camera2:1.3.0'
implementation 'androidx.camera:camera-lifecycle:1.3.0'
implementation 'androidx.camera:camera-view:1.3.0'
```

### Permisos requeridos

```
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

<application
    android:requestLegacyExternalStorage="true">
```

## 🗄️ Base de Datos Local (SQLite)

### Tabla: usuarios
```
CREATE TABLE usuarios (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre_usuario TEXT NOT NULL UNIQUE,
  contraseña TEXT NOT NULL
);
```

### Tabla: historial
```
CREATE TABLE historial (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  texto TEXT NOT NULL,
  fecha TEXT NOT NULL,
  id_usuario INTEGER NOT NULL,
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);
```

## ☕ Clases Principales
| Clase | Función |
|-------|---------|
| `LoginActivity.java` | Gestión de inicio de sesión |
| `RegisterActivity.java` | Registro de usuarios |
| `MainActivity.java` | Cámara y OCR |
| `HistorialActivity.java` | Vista del historial |
| `FiltrosActivity.java` | Aplicación de filtros |
| `DBHelper.java` | Manejo de SQLite |
| `FiltroUtils.java` | Expresiones regulares |

## 🔑 Métodos clave de DBHelper.java

```
boolean registrarUsuario(String nombre, String contraseña);
boolean validarUsuario(String nombre, String contraseña);
int obtenerIdUsuario(String nombreUsuario);
boolean insertarTexto(String texto, String fecha, int idUsuario);
Cursor obtenerHistorialPorUsuario(int idUsuario);
```

## 🔄 Flujo de la Aplicación
1. El usuario abre la app y ve el **login**.  
2. Puede registrarse si no tiene cuenta.  
3. Tras iniciar sesión, ingresa a la **cámara + OCR**.  
4. El texto reconocido puede ser guardado.  
5. El historial muestra únicamente los escaneos del usuario.  
6. Se pueden aplicar **filtros automáticos** al texto.
