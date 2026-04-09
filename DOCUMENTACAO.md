# 📄 Documentação do Aplicativo — Saúde Transparente (Mais Saúde)

> **Versão do documento:** 1.0  
> **Versão do aplicativo:** 1.2.0  
> **Data:** Março/Abril 2026  
> **Autor:** Claudio Matheus ([@ClaudioMatheusDev](https://github.com/ClaudioMatheusDev))

---

## Sumário

1. [Descrição do Aplicativo](#1-descrição-do-aplicativo)
2. [Objetivos](#2-objetivos)
3. [Público-Alvo](#3-público-alvo)
4. [Telas do Aplicativo](#4-telas-do-aplicativo)
   - 4.1 [Tela Inicial (Home)](#41-tela-inicial-home)
   - 4.2 [Novo Registro (Denúncia/Solicitação)](#42-novo-registro-denúnciasolicitação)
   - 4.3 [Acompanhamento de Registros](#43-acompanhamento-de-registros)
   - 4.4 [Perfil do Usuário](#44-perfil-do-usuário)
   - 4.5 [Navegação Global (Bottom Navigation)](#45-navegação-global-bottom-navigation)
5. [Bibliotecas e APIs Utilizadas](#5-bibliotecas-e-apis-utilizadas)
6. [Arquitetura Técnica](#6-arquitetura-técnica)
7. [Modelo de Dados](#7-modelo-de-dados)
8. [Permissões do Aplicativo](#8-permissões-do-aplicativo)
9. [Design System](#9-design-system)
10. [Funcionalidades Futuras (Roadmap)](#10-funcionalidades-futuras-roadmap)

---

## 1. Descrição do Aplicativo

O **Saúde Transparente** (nome interno: *App Mais Saúde*) é um aplicativo Android nativo desenvolvido em **Java** para facilitar a comunicação entre cidadãos e os serviços de saúde pública. Por meio dele, qualquer pessoa pode registrar denúncias, reclamações ou solicitações relacionadas às unidades de saúde próximas, acompanhar o andamento dos seus registros e gerenciar seu perfil pessoal.

O aplicativo funciona 100% de forma **offline** na versão atual, com toda a persistência de dados realizada localmente no dispositivo.

| Dado | Valor |
|------|-------|
| Nome do App | Saúde Transparente / Mais Saúde |
| Plataforma | Android |
| Linguagem | Java 17 |
| SDK Mínimo | API 24 (Android 7.0 Nougat) |
| SDK Alvo | API 35 (Android 15) |
| Sistema de Build | Gradle 8.10.2 (Kotlin DSL) |
| Namespace | `com.example.appmaissaude` |

---

## 2. Objetivos

### 2.1 Objetivo Geral

Criar uma plataforma mobile acessível para que cidadãos possam **registrar e acompanhar problemas e solicitações nos serviços de saúde pública**, promovendo transparência, controle social e melhoria contínua do atendimento.

### 2.2 Objetivos Específicos

| # | Objetivo |
|---|---------|
| 1 | Permitir que o cidadão registre denúncias/solicitações de forma simples, rápida e categorizada. |
| 2 | Oferecer acompanhamento visual do status de cada registro (Pendente, Em Análise, Resolvido). |
| 3 | Possibilitar anexar fotos como evidência dos problemas relatados. |
| 4 | Organizar as ocorrências por 9 categorias de saúde pública. |
| 5 | Manter persistência local dos dados para uso mesmo sem conexão à internet. |
| 6 | Proporcionar uma interface intuitiva e acessível seguindo o Material Design. |
| 7 | Servir como base para integração futura com APIs governamentais (ex.: gov.br). |

---

## 3. Público-Alvo

- Cidadãos brasileiros usuários do SUS (Sistema Único de Saúde).
- Pessoas com dispositivos Android 7.0 ou superior.
- Usuários sem necessidade de conhecimento técnico (interface simples e direta).

---

## 4. Telas do Aplicativo

### 4.1 Tela Inicial (Home)

**Activity:** `MainActivity.java`  
**Layout:** `activity_main.xml`

---

**Descrição visual:**

```
┌─────────────────────────────────┐
│       SAÚDE TRANSPARENTE        │  ← Header teal (#00A38D)
│                                 │
│  ┌─────────────────────────┐    │
│  │ 👤  MEUS DADOS          │    │  ← Card: dados do usuário
│  │  João Silva      [ 0 ]  │    │
│  │  ID: 12345678 Registros │    │
│  └─────────────────────────┘    │
│                                 │
│  ┌─────────────────────────┐    │
│  │  COMO PODEMOS            │   │  ← Card central de ação
│  │  AJUDAR HOJE?            │   │
│  │                          │   │
│  │ [+  NOVO REGISTRO      ] │   │  ← Botão preenchido (teal)
│  │ [ VER MEUS REGISTROS  ] │    │  ← Botão contornado (teal)
│  └─────────────────────────┘    │
│                                 │
│ [🏠][➕][📋][🔔][👤]           │  ← Bottom Navigation
└─────────────────────────────────┘
```

**Componentes:**
- **Header teal:** Exibe o título do aplicativo "SAÚDE TRANSPARENTE".
- **Card "Meus Dados":** Mostra foto de perfil (ícone circular), nome do usuário, ID e contador dinâmico de registros. O contador é atualizado em tempo real via `onResume()`.
- **Card de Ação:** Título motivacional + dois botões de navegação: "NOVO REGISTRO" (abre formulário) e "VER MEUS REGISTROS" (abre lista de acompanhamento).
- **Bottom Navigation:** Navegação para as 5 seções principais.

---

### 4.2 Novo Registro (Denúncia/Solicitação)

**Activity:** `NovoRegistroActivity.java`  
**Layout:** `activity_novo_registro.xml`  
**Modo duplo:** Criar novo registro OU editar registro existente.

---

**Descrição visual:**

```
┌─────────────────────────────────┐
│  ← 🔙     DENUNCIA              │  ← Header teal com título dinâmico
├─────────────────────────────────┤
│                                 │
│  PASSO 1: LOCALIZAÇÃO           │
│  ┌───────────────────────────┐  │
│  │  [mapa placeholder]       │  │  ← Imagem de mapa (placeholder)
│  └───────────────────────────┘  │
│  SELECIONE O CENTRO DE SAÚDE:   │
│  [ Spinner ▼ ]                  │  ← Dropdown com 5 unidades
│                                 │
│  PASSO 2: CATEGORIA             │
│  ┌──────┬──────┬──────┐         │
│  │ 🏗️   │ 💊   │ 👨‍⚕️  │         │  ← Grade 3x3 de categorias
│  │Infra │Medic.│Atend.│         │
│  ├──────┼──────┼──────┤         │
│  │ 📅   │ 💉   │ 🚑   │         │
│  │Agend.│Vacin.│Trans.│         │
│  ├──────┼──────┼──────┤         │
│  │ 🧹   │ 👥   │ ♿   │         │
│  │Limp. │ Fila │Acess.│         │
│  └──────┴──────┴──────┘         │
│                                 │
│  PASSO 3: DETALHES              │
│  ┌───────────────────────────┐  │
│  │ Descreva o que aconteceu  │  │  ← EditText (mín. 10 chars)
│  └───────────────────────────┘  │
│                                 │
│  ADICIONAR FOTOS/VÍDEOS:        │
│  [📷][🖼️][  ][  ]              │  ← Seletor de imagem da galeria
│                                 │
│  [      ENVIAR / ATUALIZAR    ] │  ← Botão teal
│                                 │
│ [🏠][➕][📋][🔔][👤]           │  ← Bottom Navigation
└─────────────────────────────────┘
```

**Componentes:**
- **Header dinâmico:** Título muda para "EDITAR REGISTRO" no modo de edição.
- **Passo 1 – Localização:** Spinner com 5 centros de saúde pré-cadastrados:
  - UBS Central
  - Hospital Regional
  - Posto de Saúde Vila Nova
  - UPA 24h Centro
  - Centro de Saúde da Família
- **Passo 2 – Categoria:** Grade 3×3 com 9 categorias clicáveis. A categoria selecionada recebe destaque visual (borda verde).
- **Passo 3 – Detalhes:** Campo de texto com validação de comprimento mínimo de 10 caracteres.
- **Anexo de fotos:** Botão para abrir a galeria; preview da imagem selecionada é exibido ao lado.
- **Botão de envio:** "ENVIAR" (novo registro) ou "ATUALIZAR" (edição). Realiza validação antes de salvar.

**Categorias disponíveis:**

| Categoria | ID de Layout |
|-----------|-------------|
| Infraestrutura | `catInfra` |
| Medicamentos | `catMedicamentos` |
| Atendimento | `catAtendimento` |
| Agendamento | `catAgendamento` |
| Vacinação | `catVacinacao` |
| Transporte | `catTransporte` |
| Limpeza | `catLimpeza` |
| Gestão de Fila | `catFila` |
| Acessibilidade | `catAcessibilidade` |

---

### 4.3 Acompanhamento de Registros

**Activity:** `AcompanhamentoActivity.java`  
**Layout:** `activity_acompanhamento.xml` + `item_registro.xml`

---

**Descrição visual (tela com registros):**

```
┌─────────────────────────────────┐
│  ← 🔙  ACOMPANHAMENTO           │  ← Header teal
├─────────────────────────────────┤
│  MEUS REGISTROS EM ANDAMENTO    │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ INFRAESTRUTURA   [PENDENTE] │ │  ← Badge colorido (laranja)
│ │ Local: UBS Central     🖊️🗑️│ │  ← Botões editar/excluir
│ │ Calçada quebrada na entrada │ │
│ │ 📅 23/03/2026 14:30         │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ MEDICAMENTOS  [EM ANÁLISE]  │ │  ← Badge azul
│ │ Local: Hospital Regional 🖊️🗑│ │
│ │ Falta de antibióticos...    │ │
│ │ 📅 22/03/2026 09:15         │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ ATENDIMENTO    [RESOLVIDO]  │ │  ← Badge verde
│ │ Local: UPA 24h Centro   🖊️🗑│ │
│ └─────────────────────────────┘ │
│                                 │
│ [🏠][➕][📋][🔔][👤]           │  ← Bottom Navigation
└─────────────────────────────────┘
```

**Descrição visual (sem registros):**

```
┌─────────────────────────────────┐
│  ← 🔙  ACOMPANHAMENTO           │
├─────────────────────────────────┤
│                                 │
│        📋 Nenhum registro       │
│           ainda.                │
│                                 │
│  Toque em 'Novo Registro'       │
│      para começar!              │
│                                 │
│ [🏠][➕][📋][🔔][👤]           │
└─────────────────────────────────┘
```

**Componentes de cada item (card):**
- **Categoria:** exibida em destaque teal, em negrito.
- **Badge de status:** toque no badge alterna o status na sequência: Pendente → Em Análise → Resolvido → Pendente.
- **Local:** nome da unidade de saúde.
- **Descrição:** texto da ocorrência.
- **Data/hora:** gerada automaticamente no momento do registro.
- **Botão editar (🖊️):** abre `NovoRegistroActivity` em modo edição com os dados preenchidos.
- **Botão excluir (🗑️):** remove o registro da lista permanentemente.

**Sistema de Status Visual:**

| Status | Cor | Código Hex |
|--------|-----|------------|
| 🟠 Pendente | Laranja | `#FF9800` |
| 🔵 Em Análise | Azul | `#2196F3` |
| 🟢 Resolvido | Verde | `#4CAF50` |

---

### 4.4 Perfil do Usuário

**Activity:** `PerfilActivity.java`  
**Layout:** `activity_perfil.xml`

---

**Descrição visual:**

```
┌─────────────────────────────────┐
│        MEU PERFIL               │  ← Header teal (180dp)
│           👤                    │  ← Avatar circular
├─────────────────────────────────┤
│                                 │
│  ┌─────────────────────────┐    │
│  │  INFORMAÇÕES PESSOAIS   │    │  ← Card 1
│  │  Nome Completo          │    │
│  │  [________________]     │    │
│  │  CPF                    │    │
│  │  [000.000.000-00  ]     │    │
│  │  Telefone               │    │
│  │  [(00) 00000-0000 ]     │    │
│  └─────────────────────────┘    │
│                                 │
│  ┌─────────────────────────┐    │
│  │  ACESSO E SEGURANÇA     │    │  ← Card 2
│  │  E-mail                 │    │
│  │  [seu@email.com   ]     │    │
│  │  Senha                  │    │
│  │  [•••••••••••    👁]    │    │
│  │  Confirmar Senha        │    │
│  │  [•••••••••••    👁]    │    │
│  └─────────────────────────┘    │
│                                 │
│  [      SALVAR PERFIL         ] │  ← Botão teal
│                                 │
│ [🏠][➕][📋][🔔][👤]           │  ← Bottom Navigation
└─────────────────────────────────┘
```

**Campos do formulário:**
- Nome Completo (máximo 60 caracteres)
- CPF (formato `000.000.000-00`, máximo 14 caracteres)
- Telefone (formato `(00) 00000-0000`, máximo 15 caracteres)
- E-mail (máximo 100 caracteres)
- Senha (mínimo 6 caracteres, toggle de visibilidade)
- Confirmar Senha (toggle de visibilidade)

> **Nota:** A tela de perfil está implementada em layout, com a lógica de salvamento em desenvolvimento.

---

### 4.5 Navegação Global (Bottom Navigation)

**Menu:** `bottom_navigation_menu.xml`  
**Cor:** `bottom_nav_color.xml` (teal quando selecionado, cinza quando inativo)

Presente em todas as telas principais:

| Ícone | Rótulo | Destino |
|-------|--------|---------|
| 🏠 | Início | `MainActivity` |
| ➕ | Novo | `NovoRegistroActivity` |
| 📋 | Histórico | `AcompanhamentoActivity` |
| 🔔 | Alertas | *(em desenvolvimento)* |
| 👤 | Perfil | `PerfilActivity` |

---

## 5. Bibliotecas e APIs Utilizadas

### 5.1 Dependências do Projeto

| Biblioteca | Versão | Finalidade |
|------------|--------|-----------|
| **Gson** (Google) | `2.10.1` | Serialização/desserialização de objetos Java para JSON, utilizada para persistir os registros via `SharedPreferences`. |
| **Material Components** (Google) | `1.9.0` | Componentes de UI seguindo o Material Design: `BottomNavigationView`, `CardView`, `TextInputLayout`, `MaterialButton`, etc. |
| **AndroidX AppCompat** | `1.7.0` | Compatibilidade retroativa de funcionalidades do Android para APIs mais antigas. |
| **AndroidX Core KTX** | `1.13.1` | Extensões Kotlin/Java para APIs do Android Core. |
| **ConstraintLayout** | `2.1.4` | Layout flexível e eficiente para construir interfaces complexas sem aninhamento excessivo. |
| **RecyclerView** | `1.3.2` | Lista rolável de alto desempenho para exibir os registros do usuário. |
| **Activity** (AndroidX) | *(via BOM)* | Suporte a Activities modernas com APIs como `ActivityResultLauncher`. |

### 5.2 APIs Android do Sistema

| API / Recurso | Uso no Aplicativo |
|---------------|-------------------|
| **SharedPreferences** | Armazenamento local de todos os registros serializados como JSON. Chave principal: `"registros_saude"`. |
| **Intent / ActivityResultLauncher** | Navegação entre Activities e obtenção de resultado da galeria de imagens. |
| **Parcelable** | Passagem de objetos `Registro` entre Activities (ex.: tela de edição). |
| **MediaStore / ACTION_PICK** | Abertura da galeria de fotos nativa do dispositivo para seleção de imagem. |
| **BitmapFactory + FileOutputStream** | Decodificação e compressão das imagens selecionadas (JPEG, qualidade 80%) para armazenamento interno. |
| **SimpleDateFormat** | Geração automática de timestamp (`dd/MM/yyyy HH:mm`) no momento de criação de cada registro. |
| **Context.getFilesDir()** | Armazenamento de imagens em pasta privada do aplicativo no armazenamento interno. |

### 5.3 Permissões Declaradas

| Permissão | Versão Android | Finalidade |
|-----------|---------------|-----------|
| `READ_MEDIA_IMAGES` | Android 13+ (API 33+) | Leitura de imagens da galeria em versões recentes. |
| `READ_EXTERNAL_STORAGE` | Android < 13 (API ≤ 32) | Leitura de imagens da galeria em versões anteriores. |

### 5.4 Recursos Nativos do Android Utilizados

| Recurso | Uso |
|---------|-----|
| **Vector Drawables** (SVG/XML) | 9 ícones customizados de categorias desenhados em XML. |
| **Shape Drawables** | Backgrounds customizados: botões, badges de status, cards, círculos de perfil. |
| **Color State List** | Seletor de cor da Bottom Navigation (teal/cinza). |
| **RecyclerView.Adapter** | Adapter customizado (`RegistroAdapter`) com `ViewHolder` pattern. |
| **Enum** | `StatusRegistro` com lógica de progressão de estados e formatação. |

---

## 6. Arquitetura Técnica

O projeto segue uma arquitetura **MVC simplificada** (Model-View-Controller), adequada para um MVP Android nativo:

```
┌─────────────────────────────────────────────────────────┐
│                       VIEW (XML Layouts)                 │
│  activity_main.xml │ activity_novo_registro.xml          │
│  activity_acompanhamento.xml │ activity_perfil.xml       │
│  item_registro.xml                                       │
└──────────────────────────┬──────────────────────────────┘
                           │ inflate / bind
┌──────────────────────────▼──────────────────────────────┐
│                    CONTROLLER (Activities)               │
│  MainActivity │ NovoRegistroActivity                     │
│  AcompanhamentoActivity │ PerfilActivity                 │
│  RegistroAdapter (RecyclerView)                          │
└──────────────────────────┬──────────────────────────────┘
                           │ read / write
┌──────────────────────────▼──────────────────────────────┐
│                      MODEL (Data Layer)                  │
│  Registro.java (entidade)                                │
│  StatusRegistro.java (enum)                              │
│  GerenciadorDados.java (repositório local)               │
│        └── SharedPreferences + Gson + FileSystem         │
└─────────────────────────────────────────────────────────┘
```

### Estrutura de Packages e Arquivos

```
app/src/main/
├── java/com/example/appmaissaude/
│   ├── MainActivity.java           # Tela inicial
│   ├── NovoRegistroActivity.java   # Formulário criar/editar registro
│   ├── AcompanhamentoActivity.java # Lista de registros
│   ├── PerfilActivity.java         # Tela de perfil do usuário
│   ├── Registro.java               # Modelo de dados (implementa Parcelable)
│   ├── StatusRegistro.java         # Enum: PENDENTE, EM_ANALISE, RESOLVIDO
│   ├── RegistroAdapter.java        # Adapter RecyclerView com ViewHolder
│   └── GerenciadorDados.java       # Persistência: SharedPreferences + Gson
│
└── res/
    ├── drawable/                   # Ícones e backgrounds customizados
    │   ├── ic_infraestrutura.xml
    │   ├── ic_medicamentos.xml
    │   ├── ic_atendimento.xml
    │   ├── ic_agendamento.xml
    │   ├── ic_vacinacao.xml
    │   ├── ic_transporte.xml
    │   ├── ic_limpeza.xml
    │   ├── ic_fila.xml
    │   ├── ic_acessibilidade.xml
    │   ├── bg_status_pendente.xml
    │   ├── bg_status_em_analise.xml
    │   ├── bg_status_resolvido.xml
    │   ├── bg_button_filled.xml
    │   ├── bg_button_outline.xml
    │   ├── bg_card_white.xml
    │   ├── bg_header_teal.xml
    │   ├── bg_icon_category.xml
    │   └── bg_status_circle.xml
    ├── layout/                     # Layouts das telas e itens
    ├── menu/
    │   └── bottom_navigation_menu.xml
    ├── color/
    │   └── bottom_nav_color.xml
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── themes.xml
```

---

## 7. Modelo de Dados

### Classe `Registro.java`

Representa uma ocorrência registrada pelo usuário. Implementa `Parcelable` para passagem entre Activities.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | `String` | Identificador único baseado em timestamp (`String.valueOf(System.currentTimeMillis())`) |
| `categoria` | `String` | Categoria da ocorrência (ex.: "Infraestrutura") |
| `local` | `String` | Nome do centro de saúde selecionado |
| `descricao` | `String` | Texto descritivo da ocorrência (mínimo 10 caracteres) |
| `caminhoImagem` | `String` | Caminho absoluto da imagem salva no armazenamento interno |
| `data` | `String` | Data e hora de criação no formato `dd/MM/yyyy HH:mm` |
| `status` | `String` | Status atual: `"PENDENTE"`, `"EM_ANALISE"` ou `"RESOLVIDO"` |

### Enum `StatusRegistro.java`

| Valor | Texto exibido | Cor |
|-------|--------------|-----|
| `PENDENTE` | "Pendente" | `#FF9800` (laranja) |
| `EM_ANALISE` | "Em Análise" | `#2196F3` (azul) |
| `RESOLVIDO` | "Resolvido" | `#4CAF50` (verde) |

**Progressão de status:** `PENDENTE → EM_ANALISE → RESOLVIDO → PENDENTE` (cíclico, via método `proximo()`).

### Classe `GerenciadorDados.java`

Camada de acesso a dados com os seguintes métodos principais:

| Método | Descrição |
|--------|-----------|
| `salvarRegistros(List<Registro>)` | Serializa a lista para JSON e salva em SharedPreferences |
| `carregarRegistros()` | Carrega e desserializa a lista de registros do SharedPreferences |
| `atualizarRegistro(Registro)` | Atualiza um registro existente identificado pelo `id` |
| `salvarImagem(Uri, Context)` | Copia e comprime a imagem (JPEG 80%) para o armazenamento interno privado |

---

## 8. Permissões do Aplicativo

Declaradas em `AndroidManifest.xml`:

```xml
<!-- Acesso à galeria de fotos (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Acesso ao armazenamento externo (Android < 13) -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

O aplicativo **não requer permissões de internet**, câmera, localização precisa ou outros recursos sensíveis em sua versão atual.

---

## 9. Design System

### 9.1 Paleta de Cores

| Papel | Cor | Hex |
|-------|-----|-----|
| Primária | Teal | `#00A38D` |
| Background | Cinza claro | `#F5F5F5` |
| Cards | Branco | `#FFFFFF` |
| Texto principal | Preto | `#000000` |
| Texto secundário | Cinza | `#666666` |
| Status: Pendente | Laranja | `#FF9800` |
| Status: Em Análise | Azul | `#2196F3` |
| Status: Resolvido | Verde | `#4CAF50` |
| Erro/Exclusão | Vermelho | `#FF5252` |
| Edição | Verde claro | `#4CAF50` |

### 9.2 Tipografia

- **Fonte:** Roboto (padrão Material Design)
- **Títulos de tela:** 18sp, bold, branco
- **Labels de seção:** 10–12sp, bold, cinza
- **Texto de conteúdo:** 13–14sp, normal
- **Badges de status:** 11sp, bold, branco

### 9.3 Componentes UI

| Componente | Estilo |
|------------|--------|
| Botão primário | Background teal, texto branco, `bg_button_filled.xml` |
| Botão secundário | Borda teal 2dp, texto teal, `bg_button_outline.xml` |
| Cards | `CardView` com `cardElevation="6dp"` e `cardCornerRadius="16dp"` |
| Header | Altura variável, cor sólida teal `#00A38D` |
| Ícones de categoria | 48–64dp, background circular teal claro `#F0F8F7`, ícone teal |
| Bottom Nav | Fundo branco, ícone/texto teal (ativo) ou cinza (inativo) |
| Campos de texto | `TextInputLayout` estilo `OutlinedBox` com borda teal |

---

## 10. Funcionalidades Futuras (Roadmap)

### Alta Prioridade

- [ ] **Sistema de Notificações Push** — Firebase Cloud Messaging (FCM) para alertar sobre mudanças de status.
- [ ] **Tela de Perfil Completa** — Salvamento de dados pessoais, foto de perfil editável.
- [ ] **Sistema de Alertas** — Seção de alertas urgentes e comunicados de saúde.
- [ ] **Gráficos e Estatísticas** — MPAndroidChart para visualização de dados dos registros.
- [ ] **Busca e Filtros no Histórico** — Filtrar por categoria, status, data ou local.

### Média Prioridade

- [ ] **Modo Escuro** — Suporte ao tema escuro do Android.
- [ ] **Compartilhamento de Registros** — Exportar via WhatsApp, e-mail, etc.
- [ ] **Sistema de Feedback com Estrelas** — Avaliação da resolução das ocorrências.
- [ ] **Múltiplas Fotos por Registro** — Galeria com até 3+ imagens.
- [ ] **Mapa Interativo** — Google Maps API para geolocalização precisa das unidades.

### Baixa Prioridade

- [ ] **Integração com gov.br** — Autenticação e envio oficial de denúncias.
- [ ] **Chat/Suporte** — Canal de comunicação com a unidade de saúde.
- [ ] **Indicador de Progresso Visual** — Linha do tempo da resolução da ocorrência.
- [ ] **Upload de Vídeos** — Evidências em vídeo para ocorrências mais complexas.

---

## Referências

- [Repositório GitHub](https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM)
- [Material Design Guidelines](https://m2.material.io/design)
- [Android Developer Docs](https://developer.android.com/docs)
- [Gson Documentation](https://github.com/google/gson)
- [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/)

---

<p align="center">
  Feito com ❤️ para melhorar a saúde pública através da tecnologia
</p>

<p align="center">
  <sub>Saúde Transparente v1.2.0 — Documento v1.0 — Março/Abril 2026</sub>
</p>
