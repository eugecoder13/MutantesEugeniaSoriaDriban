# 📊 Diagramas de Secuencia - Mutant Detection API

Este documento contiene los diagramas de secuencia completos para todos los endpoints y flujos de la aplicación.

---

## 🧬 1. POST /mutant - Detección de Mutantes

### Flujo Completo (con validación y caché)

```mermaid
sequenceDiagram
    actor Cliente
    participant Controller as MutantController
    participant Validator as DnaSequenceValidator
    participant Service as MutantService
    participant Detector as MutantDetector
    participant Repo as DnaRecordRepository
    participant DB as H2 Database

    Cliente->>Controller: POST /mutant<br/>{dna: [...]}

    Note over Controller,Validator: Validación de Request
    Controller->>Validator: @Valid DnaRequest
    Validator->>Validator: validateDnaSequence()

    alt Validación Falla
        Validator-->>Controller: MethodArgumentNotValidException
        Controller-->>Cliente: 400 BAD REQUEST<br/>{error: "Invalid DNA sequence..."}
    else Validación Exitosa
        Validator-->>Controller: Validación OK

        Note over Service: Procesamiento de DNA
        Controller->>Service: analyzeDna(dna)
        Service->>Service: calculateDnaHash(dna)<br/>[SHA-256]

        Note over Service,Repo: Verificar si DNA ya existe
        Service->>Repo: findByDnaHash(hash)
        Repo->>DB: SELECT * FROM dna_verifications<br/>WHERE dna_hash = ?

        alt DNA ya existe en caché
            DB-->>Repo: DnaRecord encontrado
            Repo-->>Service: Optional<DnaRecord>
            Service-->>Controller: isMutant (desde caché)
        else DNA es nuevo
            DB-->>Repo: Empty
            Repo-->>Service: Optional.empty()

            Note over Detector: Análisis de Secuencia
            Service->>Detector: isMutant(dna)
            Detector->>Detector: convertToMatrix(dna)

            loop Para cada posición (row, col)
                Detector->>Detector: checkHorizontalMatch()
                Detector->>Detector: checkVerticalMatch()
                Detector->>Detector: checkDiagonalDownRight()
                Detector->>Detector: checkDiagonalDownLeft()

                alt Encontró 2+ secuencias
                    Note over Detector: Early Termination
                    Detector->>Detector: return true
                end
            end

            Detector-->>Service: boolean isMutant

            Note over Service,DB: Persistir Resultado
            Service->>Service: new DnaRecord(hash, isMutant)
            Service->>Repo: save(dnaRecord)
            Repo->>DB: INSERT INTO dna_verifications<br/>(dna_hash, is_mutant, verified_at)
            DB-->>Repo: Record guardado
            Repo-->>Service: DnaRecord saved
            Service-->>Controller: isMutant
        end

        Note over Controller: Determinar HTTP Status
        alt isMutant == true
            Controller->>Controller: new DnaResponse(true, "Mutant DNA detected")
            Controller-->>Cliente: 200 OK<br/>{isMutant: true, message: "..."}
        else isMutant == false
            Controller->>Controller: new DnaResponse(false, "Human DNA detected")
            Controller-->>Cliente: 403 FORBIDDEN<br/>{isMutant: false, message: "..."}
        end
    end
```

---

## 📈 2. GET /stats - Estadísticas de Verificación

### Flujo Completo

```mermaid
sequenceDiagram
    actor Cliente
    participant Controller as MutantController
    participant Service as StatsService
    participant Repo as DnaRecordRepository
    participant DB as H2 Database

    Cliente->>Controller: GET /stats

    Note over Controller,Service: Solicitar Estadísticas
    Controller->>Service: getVerificationStats()

    Note over Service,DB: Contar DNA Mutante
    Service->>Repo: countByIsMutant(true)
    Repo->>DB: SELECT COUNT(*)<br/>FROM dna_verifications<br/>WHERE is_mutant = true
    DB-->>Repo: count_mutant (ej: 40)
    Repo-->>Service: long countMutant

    Note over Service,DB: Contar DNA Humano
    Service->>Repo: countByIsMutant(false)
    Repo->>DB: SELECT COUNT(*)<br/>FROM dna_verifications<br/>WHERE is_mutant = false
    DB-->>Repo: count_human (ej: 100)
    Repo-->>Service: long countHuman

    Note over Service: Calcular Ratio
    Service->>Service: calculateRatio()

    alt countHuman == 0 && countMutant > 0
        Service->>Service: ratio = 1.0
    else countHuman == 0 && countMutant == 0
        Service->>Service: ratio = 0.0
    else countHuman > 0
        Service->>Service: ratio = round(countMutant / countHuman, 2)
    end

    Service->>Service: new StatsResponse(<br/>  countMutant,<br/>  countHuman,<br/>  ratio<br/>)

    Service-->>Controller: StatsResponse
    Controller-->>Cliente: 200 OK<br/>{<br/>  count_mutant_dna: 40,<br/>  count_human_dna: 100,<br/>  ratio: 0.4<br/>}
```

---

## 🔍 3. Algoritmo de Detección de Mutantes (Detalle)

### Flujo Interno del MutantDetector

```mermaid
sequenceDiagram
    participant Service as MutantService
    participant Detector as MutantDetector
    participant Matrix as char[][]

    Service->>Detector: isMutant(dnaSequence)

    Note over Detector: Validación Inicial
    alt dnaSequence == null || empty
        Detector-->>Service: return false
    end

    Note over Detector,Matrix: Conversión a Matriz
    Detector->>Detector: convertToMatrix(dnaSequence)
    Detector->>Matrix: char[][] dnaMatrix
    Matrix-->>Detector: matriz creada

    Detector->>Detector: sequencesFound = 0<br/>gridSize = matrix.length

    Note over Detector: Loop Principal - Single Pass
    loop row = 0 to gridSize-1
        loop col = 0 to gridSize-1
            Detector->>Matrix: currentBase = matrix[row][col]

            Note over Detector: Check Horizontal →
            alt col <= gridSize - 4
                Detector->>Detector: checkHorizontalMatch(row, col, base)
                Detector->>Matrix: matrix[row][col+1] == base?
                Detector->>Matrix: matrix[row][col+2] == base?
                Detector->>Matrix: matrix[row][col+3] == base?

                alt Todas coinciden
                    Detector->>Detector: sequencesFound++

                    alt sequencesFound > 1
                        Note over Detector: ⚡ Early Termination
                        Detector-->>Service: return true
                    end
                end
            end

            Note over Detector: Check Vertical ↓
            alt row <= gridSize - 4
                Detector->>Detector: checkVerticalMatch(row, col, base)
                Detector->>Matrix: matrix[row+1][col] == base?
                Detector->>Matrix: matrix[row+2][col] == base?
                Detector->>Matrix: matrix[row+3][col] == base?

                alt Todas coinciden
                    Detector->>Detector: sequencesFound++

                    alt sequencesFound > 1
                        Note over Detector: ⚡ Early Termination
                        Detector-->>Service: return true
                    end
                end
            end

            Note over Detector: Check Diagonal ↘
            alt row <= gridSize - 4 && col <= gridSize - 4
                Detector->>Detector: checkDiagonalDownRight(row, col, base)
                Detector->>Matrix: matrix[row+1][col+1] == base?
                Detector->>Matrix: matrix[row+2][col+2] == base?
                Detector->>Matrix: matrix[row+3][col+3] == base?

                alt Todas coinciden
                    Detector->>Detector: sequencesFound++

                    alt sequencesFound > 1
                        Note over Detector: ⚡ Early Termination
                        Detector-->>Service: return true
                    end
                end
            end

            Note over Detector: Check Diagonal ↙
            alt row <= gridSize - 4 && col >= 3
                Detector->>Detector: checkDiagonalDownLeft(row, col, base)
                Detector->>Matrix: matrix[row+1][col-1] == base?
                Detector->>Matrix: matrix[row+2][col-2] == base?
                Detector->>Matrix: matrix[row+3][col-3] == base?

                alt Todas coinciden
                    Detector->>Detector: sequencesFound++

                    alt sequencesFound > 1
                        Note over Detector: ⚡ Early Termination
                        Detector-->>Service: return true
                    end
                end
            end
        end
    end

    Note over Detector: Fin del recorrido completo
    alt sequencesFound > 1
        Detector-->>Service: return true (mutante)
    else sequencesFound <= 1
        Detector-->>Service: return false (humano)
    end
```

---

## ⚠️ 4. Manejo de Excepciones

### Flujo de Validación y Manejo de Errores

```mermaid
sequenceDiagram
    actor Cliente
    participant Controller as MutantController
    participant Validator as DnaSequenceValidator
    participant ExHandler as GlobalExceptionHandler

    Cliente->>Controller: POST /mutant<br/>{dna: ["ATGX", "CACC"]}

    Note over Controller,Validator: Bean Validation
    Controller->>Validator: @Valid DnaRequest

    Note over Validator: Validaciones
    Validator->>Validator: isNull() ❌
    Validator->>Validator: isEmpty() ❌
    Validator->>Validator: isNxN() ❌ 2x4

    Validator-->>Controller: MethodArgumentNotValidException

    Note over Controller,ExHandler: Captura Global
    Controller->>ExHandler: handleValidationException()

    ExHandler->>ExHandler: Extraer field errors
    ExHandler->>ExHandler: Crear ValidationErrorResponse<br/>{<br/>  timestamp,<br/>  status: 400,<br/>  errors: [...]<br/>}

    ExHandler-->>Cliente: 400 BAD REQUEST<br/>{<br/>  timestamp: "2025-11-24T...",<br/>  status: 400,<br/>  error: "Validation Failed",<br/>  errors: {<br/>    dna: "DNA must be NxN matrix..."<br/>  }<br/>}
```

---

## 🔐 5. Sistema de Deduplicación (SHA-256 Hashing)

### Flujo de Hash y Caché

```mermaid
sequenceDiagram
    participant Service as MutantService
    participant Hash as MessageDigest (SHA-256)
    participant Repo as DnaRecordRepository
    participant DB as H2 Database

    Note over Service: DNA recibido
    Service->>Service: dna = ["ATGCGA", "CAGTGC", ...]

    Note over Service,Hash: Generación de Hash
    Service->>Service: calculateDnaHash(dna)
    Service->>Service: joined = String.join("", dna)<br/>"ATGCGACAGTGC..."
    Service->>Hash: MessageDigest.getInstance("SHA-256")
    Hash-->>Service: digest instance
    Service->>Hash: digest.update(joined.getBytes(UTF_8))
    Hash->>Hash: SHA-256 computation
    Hash-->>Service: byte[] hashBytes
    Service->>Service: bytesToHex(hashBytes)
    Service->>Service: hash = "a3f5b8c2d1e..."<br/>(64 caracteres hex)

    Note over Service,DB: Lookup en DB
    Service->>Repo: findByDnaHash(hash)
    Repo->>DB: SELECT * FROM dna_verifications<br/>WHERE dna_hash = 'a3f5b8c2d1e...'

    alt Hash encontrado (Cache Hit)
        DB-->>Repo: DnaRecord {<br/>  id: 123,<br/>  dnaHash: "a3f5...",<br/>  isMutant: true,<br/>  verifiedAt: ...}
        Repo-->>Service: Optional<DnaRecord>
        Note over Service: ✅ Evita re-procesamiento
        Service->>Service: Retornar isMutant desde caché
    else Hash no encontrado (Cache Miss)
        DB-->>Repo: NULL
        Repo-->>Service: Optional.empty()
        Note over Service: 🔄 Procesar DNA
        Service->>Service: Ejecutar detección de mutante
        Service->>Service: Guardar resultado con hash
    end
```

---

## 🏥 6. Health Check

### Flujo Simple de Health Check

```mermaid
sequenceDiagram
    actor Cliente
    participant Controller as MutantController

    Cliente->>Controller: GET /health
    Controller-->>Cliente: 200 OK<br/>"Mutant Detection API is running"
```

---

## 📊 7. Flujo Completo de Aplicación (Vista de Alto Nivel)

### Arquitectura en Capas

```mermaid
sequenceDiagram
    actor Client
    box rgb(220, 240, 255) Presentation Layer
        participant Controller
    end
    box rgb(255, 240, 220) Business Layer
        participant Service
        participant Detector
    end
    box rgb(240, 255, 220) Persistence Layer
        participant Repository
        participant Entity
    end
    box rgb(255, 220, 220) Data Layer
        participant Database
    end

    Client->>Controller: HTTP Request
    Note over Controller: REST API<br/>Validation<br/>Exception Handling

    Controller->>Service: Business Logic Call
    Note over Service: Orchestration<br/>Hashing<br/>Caching

    Service->>Detector: Algorithm Execution
    Note over Detector: Mutant Detection<br/>Matrix Analysis<br/>Early Termination

    Detector-->>Service: Detection Result

    Service->>Repository: Data Access
    Note over Repository: JPA Repository<br/>Query Methods

    Repository->>Entity: Map to Entity
    Note over Entity: JPA Entity<br/>@PrePersist

    Repository->>Database: SQL Query
    Note over Database: H2 In-Memory<br/>UNIQUE Constraint

    Database-->>Repository: Query Result
    Repository-->>Service: Domain Object
    Service-->>Controller: Business Result
    Controller-->>Client: HTTP Response
```

---

## 🔄 8. Ciclo de Vida de una Solicitud POST /mutant

### Desde Request hasta Response

```mermaid
sequenceDiagram
    participant HTTP as HTTP Request
    participant Spring as Spring Framework
    participant Valid as Bean Validator
    participant Ctrl as @RestController
    participant Svc as @Service
    participant Algo as Algorithm
    participant Txn as @Transactional
    participant JPA as JPA/Hibernate
    participant H2 as H2 Database

    HTTP->>Spring: POST /mutant + JSON
    Spring->>Spring: Deserialize JSON → DnaRequest
    Spring->>Valid: @Valid DnaRequest
    Valid->>Valid: @ValidDnaSequence check

    alt Validation fails
        Valid-->>Spring: ConstraintViolationException
        Spring-->>HTTP: 400 + error details
    end

    Valid-->>Spring: Validation passed
    Spring->>Ctrl: detectMutant(dnaRequest)
    Ctrl->>Svc: analyzeDna(dna)

    Note over Txn: Transaction begins
    Svc->>Svc: Calculate SHA-256 hash
    Svc->>JPA: findByDnaHash(hash)
    JPA->>H2: SELECT query

    alt Cache hit
        H2-->>JPA: DnaRecord found
        JPA-->>Svc: Return cached result
    else Cache miss
        H2-->>JPA: No record
        JPA-->>Svc: Optional.empty()
        Svc->>Algo: isMutant(dna)
        Algo->>Algo: Matrix analysis
        Algo-->>Svc: boolean result
        Svc->>JPA: save(new DnaRecord)
        JPA->>H2: INSERT query
        H2-->>JPA: Record saved
    end

    Note over Txn: Transaction commits

    Svc-->>Ctrl: boolean isMutant
    Ctrl->>Ctrl: Map to DnaResponse

    alt isMutant
        Ctrl-->>Spring: 200 OK
        Spring-->>HTTP: 200 + {isMutant: true}
    else not mutant
        Ctrl-->>Spring: 403 FORBIDDEN
        Spring-->>HTTP: 403 + {isMutant: false}
    end
```

---

## 📝 Notas Técnicas

### Convenciones de Diagramas

- **Líneas sólidas (→)**: Llamadas síncronas
- **Líneas punteadas (-->)**: Respuestas/retornos
- **Cajas "alt"**: Condicionales (if/else)
- **Cajas "loop"**: Iteraciones
- **Cajas "opt"**: Operaciones opcionales
- **Notas**: Contexto adicional

### Optimizaciones Destacadas

1. **Early Termination**: El detector termina inmediatamente al encontrar 2+ secuencias
2. **Hash-Based Caching**: Evita re-procesar DNA idéntico usando SHA-256
3. **Single Pass Algorithm**: Un solo recorrido de matriz verifica todas las direcciones
4. **Boundary Checking**: Previene lecturas fuera de límites sin try-catch
5. **Transaction Management**: `@Transactional` garantiza atomicidad

### Códigos HTTP Utilizados

| Código | Endpoint | Condición |
|--------|----------|-----------|
| 200 OK | POST /mutant | DNA mutante detectado |
| 200 OK | GET /stats | Estadísticas retornadas |
| 200 OK | GET /health | Servicio activo |
| 403 FORBIDDEN | POST /mutant | DNA humano detectado |
| 400 BAD REQUEST | POST /mutant | Validación fallida |
| 500 INTERNAL ERROR | Cualquiera | Error no manejado |

---

## 🎯 Puntos Clave de Arquitectura

1. **Separación de Responsabilidades**
   - Controller: HTTP handling + validation
   - Service: Business logic + orchestration
   - Repository: Data access abstraction
   - Detector: Core algorithm implementation

2. **Inmutabilidad de Datos**
   - DTOs con Lombok `@Data`
   - Entities con `@PrePersist` hooks
   - Hash-based identity sin modificación de data

3. **Validación en Capas**
   - Bean Validation en DTOs
   - Business validation en Services
   - Database constraints en Entities

4. **Gestión de Transacciones**
   - `@Transactional` en métodos de persistencia
   - Rollback automático en excepciones
   - Consistency garantizada

---

**Generado para**: Mutant Detection API v1.0.0
**Fecha**: 2025-11-24
**Tecnología**: Spring Boot 3.3.5 + Java 17
