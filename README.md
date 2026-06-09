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

アプリケーション起動時に Flyway migration が実行されます。

migration ファイル:

```text
src/main/resources/db/migration/V1__create_tables.sql
```

## jOOQ code generation

PostgreSQL 起動後に以下を実行します。

```bash
./gradlew jooqCodegen
```

生成コードは以下に出力されます。

```text
build/generated-src/jooq/main
```

生成コードは手動編集しません。

## アプリケーション起動

```bash
./gradlew bootRun
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

## 実装補足

- 更新APIは `PUT` を使用し、部分更新ではなく全項目更新として実装しています。
- 書籍と著者は多対多関係とし、中間テーブル `book_authors` で管理しています。
- 書籍更新時の `authorIds` は差分更新ではなく全差し替え方式を採用しています。
