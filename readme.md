## Projeye Özel Dağıtım/Ağ Mimarisi (Mermaid)

```mermaid
graph LR
    subgraph Kullanici [Kullanıcı Erişimi]
        Customer[Customer Browser]
    end

    subgraph SunucuAltyapisi [Sunucu Altyapısı]
        direction TB
        Nginx[Nginx Reverse Proxy <br> (Port 80/443)]

        subgraph FrontendSunumu [Frontend Sunumu]
           AngularFiles[Angular Build Dosyaları <br> (HTML, JS, CSS, Assets)]
        end

        subgraph BackendAltyapisi [Backend Altyapısı]
           SpringBootApp[Spring Boot Backend (Anason) <br> Port: 8080 <br> Context Path: /api]
           Database[(MySQL Veritabanı <br> Port: 3306)]
        end

        subgraph DisServisler [Dış Servisler]
           StripeAPI[(Stripe API)]
        end
    end

    %% Akışlar
    Customer -- HTTPS İsteği --> Nginx

    Nginx -- Statik Dosya Sunumu (örn: /) --> AngularFiles
    Nginx -- API Çağrıları (/api/**) --> SpringBootApp

    SpringBootApp -- Veritabanı İşlemleri (JPA) --> Database
    SpringBootApp -- Ödeme İşlemleri --> StripeAPI

    %% Styling (Görselleştirme için)
    classDef user fill:#c9daf8,stroke:#333,stroke-width:2px;
    classDef proxy fill:#f9f,stroke:#333,stroke-width:2px;
    classDef frontend fill:#ccf,stroke:#333,stroke-width:2px;
    classDef backend fill:#cfc,stroke:#333,stroke-width:2px;
    classDef db fill:#ffc,stroke:#333,stroke-width:2px;
    classDef external fill:#fdc,stroke:#333,stroke-width:2px;

    class Customer user;
    class Nginx proxy;
    class AngularFiles frontend;
    class SpringBootApp backend;
    class Database db;
    class StripeAPI external;