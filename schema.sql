
    create table appointments (
        queue_position integer,
        reminder_sent bit not null,
        client_id bigint not null,
        created_at datetime(6),
        end_time datetime(6) not null,
        id bigint not null auto_increment,
        service_id bigint not null,
        start_time datetime(6) not null,
        updated_at datetime(6),
        user_centre_soin_id bigint,
        notes TEXT,
        status enum ('CANCELLED','COMPLETED','CONFIRMED','IN_PROGRESS','NO_SHOW','PENDING') not null,
        primary key (id)
    ) engine=InnoDB;

    create table centre_soin (
        active bit not null,
        is_visible bit not null,
        latitude decimal(10,7),
        longitude decimal(10,7),
        created_at datetime(6),
        id bigint not null auto_increment,
        updated_at datetime(6),
        ville_id bigint,
        address varchar(255),
        fix varchar(255),
        img varchar(255),
        quartier varchar(255),
        shop_name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table client (
        created_at datetime(6),
        id bigint not null auto_increment,
        updated_at datetime(6),
        user_centre_soin_id bigint,
        user_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table closing_days (
        closed_date date not null,
        id bigint not null auto_increment,
        user_centre_soin_id bigint not null,
        reason varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table config_user_centre_soin (
        id bigint not null auto_increment,
        user_centre_soin_id bigint,
        user_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table invoice_lines (
        quantity integer not null,
        total_price decimal(10,2) not null,
        unit_price decimal(10,2) not null,
        id bigint not null auto_increment,
        invoice_id bigint not null,
        product_id bigint,
        service_id bigint,
        label varchar(255) not null,
        line_type enum ('PRODUCT','SERVICE') not null,
        primary key (id)
    ) engine=InnoDB;

    create table invoices (
        subtotal decimal(10,2) not null,
        tax_amount decimal(10,2) not null,
        tax_rate decimal(5,2) not null,
        total decimal(10,2) not null,
        appointment_id bigint not null,
        client_id bigint not null,
        id bigint not null auto_increment,
        issued_at datetime(6) not null,
        user_centre_soin_id bigint not null,
        invoice_number varchar(255) not null,
        notes TEXT,
        pdf_url varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table notifications (
        appointment_id bigint,
        created_at datetime(6),
        id bigint not null auto_increment,
        sent_at datetime(6),
        user_id bigint not null,
        message TEXT not null,
        channel enum ('EMAIL','PUSH','SMS','WHATSAPP') not null,
        status enum ('FAILED','PENDING','SENT') not null,
        primary key (id)
    ) engine=InnoDB;

    create table opening_hours (
        close_time time(6),
        day_of_week integer not null,
        is_closed bit not null,
        open_time time(6),
        slot_duration_min integer not null,
        id bigint not null auto_increment,
        user_centre_soin_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table pays (
        id bigint not null auto_increment,
        lang varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table product_order_lines (
        quantity_ordered integer not null,
        quantity_received integer not null,
        total_cost decimal(10,2) not null,
        unit_cost decimal(10,2) not null,
        id bigint not null auto_increment,
        order_id bigint not null,
        product_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table product_orders (
        expected_date date,
        received_date date,
        total_amount decimal(10,2),
        id bigint not null auto_increment,
        order_date datetime(6) not null,
        supplier_id bigint,
        user_centre_soin_id bigint not null,
        notes TEXT,
        status enum ('CANCELLED','DRAFT','ORDERED','PARTIAL','RECEIVED') not null,
        primary key (id)
    ) engine=InnoDB;

    create table products (
        is_active bit not null,
        is_for_sale bit not null,
        price_cost decimal(10,2),
        price_sell decimal(10,2) not null,
        stock_alert_min integer not null,
        stock_quantity integer not null,
        created_at datetime(6),
        id bigint not null auto_increment,
        updated_at datetime(6),
        user_centre_soin_id bigint not null,
        category varchar(255),
        description TEXT,
        name varchar(255) not null,
        photo_url varchar(255),
        sku varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table quartier (
        id bigint not null auto_increment,
        ville_id bigint,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table refresh_tokens (
        revoked bit not null,
        created_at datetime(6),
        expires_at datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token_hash varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table review_photos (
        id bigint not null auto_increment,
        review_id bigint not null,
        uploaded_at datetime(6),
        photo_url varchar(255) not null,
        photo_type enum ('AFTER','BEFORE') not null,
        primary key (id)
    ) engine=InnoDB;

    create table reviews (
        is_visible bit not null,
        rating integer not null,
        appointment_id bigint not null,
        client_id bigint not null,
        created_at datetime(6),
        id bigint not null auto_increment,
        user_centre_soin_id bigint not null,
        comment TEXT,
        primary key (id)
    ) engine=InnoDB;

    create table services (
        display_order integer not null,
        duration_min integer not null,
        is_active bit not null,
        price decimal(10,2) not null,
        created_at datetime(6),
        id bigint not null auto_increment,
        updated_at datetime(6),
        user_centre_soin_id bigint not null,
        description TEXT,
        name varchar(255) not null,
        category enum ('BEARD','MENS_CUT','OTHER','TREATMENT','WOMENS_CUT') not null,
        primary key (id)
    ) engine=InnoDB;

    create table stock_movements (
        quantity integer not null,
        created_at datetime(6),
        id bigint not null auto_increment,
        product_id bigint not null,
        reference_id bigint,
        notes TEXT,
        reference_type varchar(255),
        movement_type enum ('ADJUSTMENT','IN','OUT','SALE','USED_IN_SERVICE') not null,
        primary key (id)
    ) engine=InnoDB;

    create table suppliers (
        created_at datetime(6),
        id bigint not null auto_increment,
        user_centre_soin_id bigint not null,
        address TEXT,
        contact_name varchar(255),
        email varchar(255),
        name varchar(255) not null,
        phone varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table type_user (
        active bit,
        id integer not null auto_increment,
        created_at datetime(6),
        deleted_at datetime(6),
        updated_at datetime(6),
        label varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table user_centre_soin (
        is_active bit,
        is_confirmed bit,
        is_visible bit,
        centre_soin_id bigint,
        id bigint not null auto_increment,
        user_id bigint,
        fix varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users (
        email_verified bit not null,
        is_active bit not null,
        type_user_id integer not null,
        created_at datetime(6),
        deleted_at datetime(6),
        id bigint not null auto_increment,
        pays_id bigint,
        quartier_id bigint,
        updated_at datetime(6),
        ville_id bigint,
        email varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        password_hash varchar(255) not null,
        phone varchar(255),
        profile_photo varchar(255),
        role enum ('ADMIN','BARBER','USER') not null,
        primary key (id)
    ) engine=InnoDB;

    create table ville (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    alter table closing_days 
       add constraint UKc7oovjbtbgveet0qbixd4oom unique (user_centre_soin_id, closed_date);

    alter table invoices 
       add constraint UKr1gsksmeq3yb5fipnxl93yqqv unique (appointment_id);

    alter table invoices 
       add constraint UKl1x55mfsay7co0r3m9ynvipd5 unique (invoice_number);

    alter table opening_hours 
       add constraint UKe7mqd1lxi8x3b5xx2i8vlk4fa unique (user_centre_soin_id, day_of_week);

    alter table refresh_tokens 
       add constraint UKo2mlirhldriil2y7krapq4frt unique (token_hash);

    alter table reviews 
       add constraint UKvroos1rdslok15k6q2go3p15 unique (appointment_id);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table appointments 
       add constraint FK21ahd1jev5nvuumq6rjg8eg1t 
       foreign key (client_id) 
       references client (id);

    alter table appointments 
       add constraint FK5iltr7k9pows18hk8nc101vc1 
       foreign key (service_id) 
       references services (id);

    alter table appointments 
       add constraint FKs32mcm7a7ayb2sq8v9pmpomf7 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table centre_soin 
       add constraint FKbura07xcesa084tyk1vhlauoa 
       foreign key (ville_id) 
       references ville (id);

    alter table client 
       add constraint FKbxisi412kym1baqfr00rxd8yo 
       foreign key (user_id) 
       references users (id);

    alter table client 
       add constraint FKa4v73ufil9djxoc7u4h8876xk 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table closing_days 
       add constraint FKi68kv1jupqfkr773aap992raw 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table config_user_centre_soin 
       add constraint FK4tuf83nkslqorjju4pdxwodb9 
       foreign key (user_id) 
       references users (id);

    alter table config_user_centre_soin 
       add constraint FKmyylvbitjhkt2agumpr4vivt7 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table invoice_lines 
       add constraint FKsgudq2lwpa9wc92a23nggah1w 
       foreign key (invoice_id) 
       references invoices (id);

    alter table invoice_lines 
       add constraint FKm2jo8loc0ps5q6qtlx4nx6o3e 
       foreign key (product_id) 
       references products (id);

    alter table invoice_lines 
       add constraint FKg2jwxy7kx4ciu24p5war2byvs 
       foreign key (service_id) 
       references services (id);

    alter table invoices 
       add constraint FKngg5bc8atao2b9jehl9l8tdsw 
       foreign key (appointment_id) 
       references appointments (id);

    alter table invoices 
       add constraint FKio1utq0y89stthe5fdnk3ug8q 
       foreign key (client_id) 
       references client (id);

    alter table invoices 
       add constraint FKbf2597ga1ti99bt37x8q4xse1 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table notifications 
       add constraint FKssk9idpjfjhwan3hhxle73wfo 
       foreign key (appointment_id) 
       references appointments (id);

    alter table notifications 
       add constraint FK9y21adhxn0ayjhfocscqox7bh 
       foreign key (user_id) 
       references users (id);

    alter table opening_hours 
       add constraint FKky08dal2jd0ld90wbw6wadb9e 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table product_order_lines 
       add constraint FK97dsrcux9cvjdjajlv6wnenv5 
       foreign key (order_id) 
       references product_orders (id);

    alter table product_order_lines 
       add constraint FKegpypk0o269v9jkr38swhfnw9 
       foreign key (product_id) 
       references products (id);

    alter table product_orders 
       add constraint FKiw4l22jajtf0lyochhsw3y2dx 
       foreign key (supplier_id) 
       references suppliers (id);

    alter table product_orders 
       add constraint FK2kvfewdt9jddhfl96p7ar0lvs 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table products 
       add constraint FKa04wavvpr1tyyn0w0220aewd5 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table quartier 
       add constraint FK5iktcd7acpcm546pye2gpbyt5 
       foreign key (ville_id) 
       references ville (id);

    alter table refresh_tokens 
       add constraint FK1lih5y2npsf8u5o3vhdb9y0os 
       foreign key (user_id) 
       references users (id);

    alter table review_photos 
       add constraint FKunrlxq8kevetatdevbd9xbp1 
       foreign key (review_id) 
       references reviews (id);

    alter table reviews 
       add constraint FKfhaj6kqx2pjpn6eambt0pa1nm 
       foreign key (appointment_id) 
       references appointments (id);

    alter table reviews 
       add constraint FK5bdf0v7ehhnmxwykrm1kk44sg 
       foreign key (client_id) 
       references client (id);

    alter table reviews 
       add constraint FKppw7tbt2mtq7sx446p2xg3ggb 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table services 
       add constraint FKt03as94fl4wjjro32kaddyput 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table stock_movements 
       add constraint FKjcaag8ogfjxpwmqypi1wfdaog 
       foreign key (product_id) 
       references products (id);

    alter table suppliers 
       add constraint FKqdd1747uwag8c5gfh58h9scfu 
       foreign key (user_centre_soin_id) 
       references user_centre_soin (id);

    alter table user_centre_soin 
       add constraint FKmwppdxf2iswe79suswtv6rsh5 
       foreign key (centre_soin_id) 
       references centre_soin (id);

    alter table user_centre_soin 
       add constraint FKicon76ihj312jxrq5g1y7iyrn 
       foreign key (user_id) 
       references users (id);

    alter table users 
       add constraint FKb5xgg1aly1bik4ajy5vmtypf5 
       foreign key (pays_id) 
       references pays (id);

    alter table users 
       add constraint FKfisvhlubo82caotivkbc840kh 
       foreign key (quartier_id) 
       references quartier (id);

    alter table users 
       add constraint FKas3xov25iycc7u2x86utt94vy 
       foreign key (type_user_id) 
       references type_user (id);

    alter table users 
       add constraint FKbiu440o04p9o565g2u85sqaly 
       foreign key (ville_id) 
       references ville (id);
