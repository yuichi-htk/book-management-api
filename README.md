# Book Management API

書籍と著者を管理する Kotlin + Spring Boot の REST API です。

## 使用技術

- Kotlin
- Spring Boot
- PostgreSQL
- Flyway
- jOOQ
- Gradle
- Docker Compose

## 必要環境

- Java 17
- Docker / Docker Compose

## Docker 起動

```bash
docker compose up -d
```

PostgreSQL は以下の設定で起動します。

- database: `book_management`
- user: `app`
- password: `password`
- port: `5432`

## Flyway

アプリケーションのビルド時に Gradle の `flywayMigrate` タスクで migration が実行されます。  
これにより、jOOQ code generation 前に PostgreSQL 上へ schema が作成されます。

migration ファイル:

```text
src/main/resources/db/migration/V1__create_tables.sql
```

## jOOQ code generation

PostgreSQL 起動後に以下を実行すると、事前に Flyway migration が実行されてから jOOQ のコードが生成されます。

```bash
./gradlew jooqCodegen
```

生成コードは以下に出力されます。

```text
build/generated-src/jooq/main
```

生成コードは手動編集しません。

## アプリケーション起動

クリーン環境では PostgreSQL を起動してからアプリケーションを起動します。

```bash
docker compose up -d
```

```bash
./gradlew bootRun
```

`bootRun` 実行時は、以下の順序で Gradle タスクが実行されます。

```text
flywayMigrate
  ↓
jooqCodegen
  ↓
compileKotlin
  ↓
bootRun
```

## build / test

```bash
./gradlew build
```

```bash
./gradlew test
```

## API 一覧

| Method | Path                      | 説明           |
| ------ | ------------------------- | -------------- |
| POST   | `/authors`                | 著者登録       |
| PUT    | `/authors/{authorId}`     | 著者更新       |
| POST   | `/books`                  | 書籍登録       |
| PUT    | `/books/{bookId}`         | 書籍更新       |
| GET    | `/authors/{authorId}/books` | 著者に紐づく書籍一覧取得 |

Base URL:

```text
http://localhost:8080
```

## API Example

著者を登録します。

```bash
curl -X POST http://localhost:8080/authors \
  -H "Content-Type: application/json" \
  -d '{"name":"山田太郎","birthDate":"1990-01-01"}'
```

登録した著者の `id` を指定して、書籍を登録します。

```bash
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Kotlin入門","price":3000,"authorIds":[1],"publicationStatus":"UNPUBLISHED"}'
```

著者に紐づく書籍を取得します。

```bash
curl http://localhost:8080/authors/1/books
```

## 実装補足

- 更新APIは `PUT` を使用し、部分更新ではなく全項目更新として実装しています。
- 書籍と著者は多対多関係とし、中間テーブル `book_authors` で管理しています。
- 書籍更新時の `authorIds` は差分更新ではなく全差し替え方式を採用しています。
