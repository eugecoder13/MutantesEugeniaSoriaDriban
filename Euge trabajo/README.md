# 🧬 Mutant Detection API

API REST desarrollada en Java con Spring Boot para detectar si un humano es mutante basándose en su secuencia de ADN. Proyecto creado para el programa de reclutamiento de mutantes de Magneto.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Ejecución](#ejecución)
- [Endpoints](#endpoints)
- [Testing](#testing)
- [Docker](#docker)
- [Arquitectura](#arquitectura)
- [Algoritmo](#algoritmo)

## ✨ Características

- ✅ Detección de mutantes mediante análisis de secuencias de ADN
- ✅ API REST con endpoints `/mutant` y `/stats`
- ✅ Persistencia en base de datos H2
- ✅ Deduplicación mediante hash SHA-256
- ✅ Validación de datos con Bean Validation
- ✅ Documentación automática con Swagger/OpenAPI
- ✅ Cobertura de tests >80%
- ✅ Dockerizado para fácil despliegue
- ✅ Optimizaciones de rendimiento (early termination, boundary checking)

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.3.5**
- **Gradle 8.x**
- **H2 Database** (en memoria)
- **JPA/Hibernate**
- **Lombok**
- **Swagger/OpenAPI 3**
- **JUnit 5** & **Mockito**
- **Docker**

## 📦 Requisitos Previos

- Java 17 o superior
- Gradle 8.x (o usar el wrapper incluido)
- Docker (opcional, para containerización)

## 🔧 Instalación

1. Clonar el repositorio:
```bash
git clone <repository-url>
cd mutant-detector-api
```

2. Dar permisos de ejecución al wrapper de Gradle (Linux/Mac):
```bash
chmod +x gradlew
```

## 🚀 Ejecución

### Opción 1: Usando Gradle

```bash
# Linux/Mac
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

### Opción 2: Compilar y ejecutar JAR

```bash
# Compilar
./gradlew clean build

# Ejecutar
java -jar build/libs/mutant-detector-api-1.0.0.jar
```

### Opción 3: Usando Docker

```bash
# Construir imagen
docker build -t mutant-detector-api .

# Ejecutar contenedor
docker run -p 8080:8080 mutant-detector-api
```

La aplicación estará disponible en: **http://localhost:8080**

## 📡 Endpoints

### 1. Detectar Mutante

**POST** `/mutant`

Detecta si una secuencia de ADN pertenece a un mutante.

**Request Body:**
```json
{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Responses:**
- `200 OK` - DNA mutante detectado
- `403 FORBIDDEN` - DNA humano detectado
- `400 BAD REQUEST` - Formato de DNA inválido

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
  }'
```

### 2. Obtener Estadísticas

**GET** `/stats`

Retorna estadísticas de las verificaciones de ADN.

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

**Ejemplo con cURL:**
```bash
curl http://localhost:8080/stats
```

### 3. Health Check

**GET** `/health`

Verifica el estado del servicio.

**Response:** `200 OK` - "Mutant Detection API is running"

## 📊 Swagger UI

La documentación interactiva de la API está disponible en:

**http://localhost:8080/swagger-ui.html**

También puedes acceder al JSON de OpenAPI en:

**http://localhost:8080/api-docs**

## 🧪 Testing

### Ejecutar todos los tests

```bash
./gradlew test
```

### Ejecutar tests con reporte de cobertura

```bash
./gradlew test jacocoTestReport
```

El reporte de cobertura estará disponible en:
`build/reports/jacoco/test/html/index.html`

### Ejecutar tests específicos

```bash
# Tests del detector de mutantes
./gradlew test --tests MutantDetectorTest

# Tests del servicio
./gradlew test --tests MutantServiceTest

# Tests del controller
./gradlew test --tests MutantControllerTest
```

### Suite de Tests

- **MutantDetectorTest**: 17 tests unitarios
  - Detección horizontal, vertical, diagonal
  - Casos edge (null, empty, matrices grandes)
  - Validación de early termination

- **MutantServiceTest**: 5 tests
  - Análisis de DNA nuevo
  - Caché de resultados
  - Hash consistency

- **StatsServiceTest**: 6 tests
  - Cálculo de estadísticas
  - Casos edge (división por cero)
  - Redondeo de ratios

- **MutantControllerTest**: 8 tests de integración
  - Endpoints POST /mutant
  - Endpoint GET /stats
  - Validación de datos

**Total: 36+ tests con cobertura >80%**

## 🐳 Docker

### Construir imagen

```bash
docker build -t mutant-detector-api:1.0.0 .
```

### Ejecutar contenedor

```bash
docker run -d \
  --name mutant-api \
  -p 8080:8080 \
  mutant-detector-api:1.0.0
```

### Ver logs

```bash
docker logs -f mutant-api
```

### Detener y eliminar

```bash
docker stop mutant-api
docker rm mutant-api
```

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas:

```
src/main/java/com/magneto/
├── controller/          # Endpoints REST
│   └── MutantController.java
├── service/            # Lógica de negocio
│   ├── MutantDetector.java
│   ├── MutantService.java
│   └── StatsService.java
├── repository/         # Acceso a datos
│   └── DnaRecordRepository.java
├── entity/            # Entidades JPA
│   └── DnaRecord.java
├── dto/               # Data Transfer Objects
│   ├── DnaRequest.java
│   ├── DnaResponse.java
│   └── StatsResponse.java
├── validation/        # Validadores personalizados
│   ├── ValidDnaSequence.java
│   └── DnaSequenceValidator.java
├── exception/         # Manejo de excepciones
│   ├── DnaProcessingException.java
│   └── GlobalExceptionHandler.java
├── config/            # Configuración
│   └── OpenApiConfig.java
└── MutantDetectorApplication.java
```

### Patrones Implementados

- **Dependency Injection**: Mediante `@RequiredArgsConstructor` de Lombok
- **Repository Pattern**: Con Spring Data JPA
- **DTO Pattern**: Separación de modelos de dominio y transferencia
- **Strategy Pattern**: En el algoritmo de detección

## 🧬 Algoritmo

El algoritmo de detección implementa las siguientes optimizaciones:

### 1. Early Termination
```java
if (sequencesFound > 1) return true;
```
Se detiene inmediatamente al encontrar más de una secuencia.

### 2. Conversión a Matriz de Chars
```java
char[][] dnaMatrix = convertToMatrix(dnaSequence);
```
Acceso O(1) a elementos individuales.

### 3. Boundary Checking
```java
if (col <= gridSize - SEQUENCE_LENGTH) {
    // Check horizontal
}
```
Evita verificaciones innecesarias fuera de límites.

### 4. Direct Comparison
```java
return matrix[row][col+1] == base &&
       matrix[row][col+2] == base &&
       matrix[row][col+3] == base;
```
Comparación directa sin loops adicionales.

### 5. Single Pass
Un solo recorrido de la matriz verifica todas las direcciones:
- Horizontal →
- Vertical ↓
- Diagonal ↘
- Diagonal ↙

### Complejidad

- **Temporal**: O(N²) en el peor caso, pero con early termination típicamente mucho menor
- **Espacial**: O(N²) para la matriz de chars, sin estructuras auxiliares

## 💾 Base de Datos

### H2 Console

Acceder a la consola H2 en: **http://localhost:8080/h2-console**

**Configuración:**
- JDBC URL: `jdbc:h2:mem:mutantdb`
- Username: `magneto`
- Password: `xmen2024`

### Esquema

```sql
CREATE TABLE dna_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dna_hash VARCHAR(64) UNIQUE NOT NULL,
    is_mutant BOOLEAN NOT NULL,
    verified_at TIMESTAMP NOT NULL
);
```

### Deduplicación

Se utiliza SHA-256 para generar un hash único de cada secuencia de ADN, evitando duplicados:

```java
String hash = calculateDnaHash(dnaSequence);
Optional<DnaRecord> existing = repository.findByDnaHash(hash);
```

## 📈 Cobertura de Tests

Mínimo requerido: **80%**

Para verificar la cobertura:

```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

Objetivos de cobertura por componente:
- **MutantDetector**: >95%
- **Services**: >90%
- **Controller**: >85%
- **Total**: >80%

## 🔍 Validaciones

El sistema valida que:
- ✅ La matriz sea NxN (cuadrada)
- ✅ Solo contenga caracteres A, T, C, G
- ✅ No sea null o vacía
- ✅ Todas las filas tengan la misma longitud

## 🚦 Estados HTTP

| Código | Significado | Cuándo |
|--------|-------------|--------|
| 200 OK | Mutante detectado | DNA tiene >1 secuencia |
| 403 FORBIDDEN | Humano detectado | DNA tiene ≤1 secuencia |
| 400 BAD REQUEST | Datos inválidos | Formato incorrecto |
| 500 INTERNAL ERROR | Error del servidor | Error inesperado |

## 📝 Logs

La aplicación genera logs estructurados:

```
INFO  - Analyzing DNA sequence of size: 6
DEBUG - Calculated DNA hash: a3f5b8...
INFO  - New DNA analyzed. Result: isMutant=true
DEBUG - DNA record saved with hash: a3f5b8...
```

## 🔧 Configuración

Variables principales en `application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:mutantdb
spring.h2.console.enabled=true
logging.level.com.magneto=INFO
```

## 👥 Autor

Proyecto desarrollado para el examen técnico de Mercado Libre - Nivel 3

## 📄 Licencia

MIT License
