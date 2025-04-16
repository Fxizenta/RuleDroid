package com.mmt.travel.app.common.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.mmt.travel.app.common.provider.MMTContentProvider;
import com.mmt.travel.app.common.util.LogUtils;
import com.mmt.travel.app.common.util.at;
import com.mmt.travel.app.homepage.e.h;
import io.hansel.pebbletracesdk.HanselCrashReporter;
import io.hansel.pebbletracesdk.annotations.HanselInclude;
import io.hansel.pebbletracesdk.codepatch.PatchJoinPoint;
import io.hansel.pebbletracesdk.codepatch.patch.Patch;

@HanselInclude
/* loaded from: classes.dex */
public class a extends SQLiteOpenHelper {

    /* renamed from: a, reason: collision with root package name */
    public static final Uri f4334a = Uri.parse("content://com.mmt.travel.app");
    private static a b;
    private final String A;
    private final String B;
    private final String C;
    private final String D;
    private final String E;
    private final String F;
    private final String G;
    private final String H;
    private final String I;
    private final String J;
    private final String K;
    private final String L;
    private final String M;
    private final String N;
    private final String O;
    private final String P;
    private final String Q;
    private final String R;
    private final String S;
    private final String T;
    private final String U;
    private final String V;
    private final String W;
    private final String X;
    private final String Y;
    private final String Z;
    private final String aA;
    private final String aB;
    private final String aC;
    private final String aD;
    private final String aE;
    private final String aF;
    private final String aG;
    private final String aH;
    private final String aa;
    private final String ab;
    private final String ac;
    private final String ad;
    private final String ae;
    private final String af;
    private final String ag;
    private final String ah;
    private final String ai;
    private final String aj;
    private final String ak;
    private final String al;
    private final String am;
    private final String an;
    private final String ao;
    private final String ap;
    private final String aq;
    private final String ar;
    private final String as;
    private final String at;
    private final String au;
    private final String av;
    private final String aw;
    private final String ax;
    private final String ay;
    private final String az;
    private final String c;
    private final String d;
    private final String e;
    private final String f;
    private final String g;
    private final String h;
    private final String i;
    private final String j;
    private final String k;
    private final String l;
    private final String m;
    private final String n;
    private final String o;
    private final String p;
    private final String q;
    private final String r;
    private final String s;
    private final String t;
    private final String u;
    private final String v;
    private final String w;
    private final String x;
    private final String y;
    private final String z;

    public static synchronized a a(Context context, MMTContentProvider.b bVar) {
        a aVar;
        synchronized (a.class) {
            Patch patch = HanselCrashReporter.getPatch(a.class, "a", Context.class, MMTContentProvider.b.class);
            if (patch != null) {
                aVar = (a) patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(a.class).setArguments(new Object[]{context, bVar}).toPatchJoinPoint());
            } else {
                if (b == null) {
                    b = new a(context.getApplicationContext(), bVar);
                }
                aVar = b;
            }
        }
        return aVar;
    }

    private a(Context context, MMTContentProvider.b bVar) {
        super(context, "makemytrip_db", bVar, 34);
        this.c = LogUtils.a(a.class);
        this.d = "create table ";
        this.e = " text not null,";
        this.f = "create table if not exists get_config_table( _id integer primary key autoincrement,get_config_key text not null, get_config_value text not null)";
        this.g = "DROP TABLE IF EXISTS master_image_cache";
        this.h = "create table flight_service_table( _id integer primary key autoincrement,key text not null,value text not null)";
        this.i = "DROP TABLE IF EXISTS flight_service_table";
        this.j = "create table if not exists flight_dyn_key_val_table( _id integer primary key autoincrement,key text not null,value text not null)";
        this.k = "DROP TABLE IF EXISTS flight_dyn_key_val_table";
        this.l = "create table if not exists flight_updater_table( _id integer primary key autoincrement,key text not null,value LONG not null)";
        this.m = "DROP TABLE IF EXISTS flight_updater_table";
        this.n = "DROP TABLE IF EXISTS favourite_search_flights_table";
        this.o = "create table fare_alert_table( _id integer primary key autoincrement,fa_id text                                                                                                                                                                                                                                                                                                                                          not null, fa_msg_count int not null, fa_increment_count int not null, fa_hashcode text)";
        this.p = "create table if_fare_alert_table( _id integer primary key autoincrement,fa_id text                                                                                                                                                                                                                                                                                                                                          not null, fa_msg_count int not null, fa_increment_count int not null, fa_hashcode text)";
        this.q = "DROP TABLE IF EXISTS recent_search_flights_table";
        this.r = "create table city_airport_data_flights_table( _id integer primary key autoincrement,id_data int not null, code text not null,city_name text,country_name text not null,synonyms text not null,description text not null,airport_data text not null,city_type text not null,mapping_type text not null)";
        this.s = "create table personalized_home_page_table (key text primary key,response text,request text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)";
        this.t = "create table personalized_hotel_landing_table (key text primary key,response text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)";
        this.u = "create table hotel_static_persuasions (persuasion_id text primary key not null,persuasion_desc text,persuasion_page_name text,persuasion_placeholder text,persuasion_priority integer default 0)";
        this.v = "create table app_config_data (data_id text primary key not null,data_string text)";
        this.w = "DROP TABLE IF EXISTS city_airport_data_flights_table";
        this.x = "create table city_mapping_flights_table( _id integer primary key autoincrement,id1_data INTEGER,id2_data int not null,mapping_type text not null)";
        this.y = "DROP TABLE IF EXISTS city_mapping_flights_table";
        this.z = "create table airline_logo_flights_table( airline_code_airline_logo text primary key, airline_logo blob not null)";
        this.A = "DROP TABLE IF EXISTS airline_logo_flights_table";
        this.B = "create table airline_code_name_table( airline_code text primary key, airline_name text not null)";
        this.C = "DROP TABLE IF EXISTS airline_code_name_table";
        this.D = "create table traveller_flights_table( _id integer primary key autoincrement,first_name text not null, last_name text ,pax_type text not null,gender text not null,age integer,title text not null,nationality text null,nationality_code text null,dob long null,passport_no text null,passport_issue_country text null,passport_issue_country_code text null,passport_expiry_date long null,locally_added integer null,pax_id integer)";
        this.E = "DROP TABLE IF EXISTS traveller_flights_table";
        this.F = "alter table traveller_flights_table add column pax_id integer";
        this.G = "alter table traveller_flights_table add column nationality text null";
        this.H = "alter table traveller_flights_table add column nationality_code text null";
        this.I = "alter table traveller_flights_table add column dob long null";
        this.J = "alter table traveller_flights_table add column passport_no text null";
        this.K = "alter table traveller_flights_table add column passport_issue_country text null";
        this.L = "alter table traveller_flights_table add column passport_issue_country_code text null";
        this.M = "alter table traveller_flights_table add column passport_expiry_date long null";
        this.N = "alter table traveller_flights_table add column locally_added integer null";
        this.O = "alter table hotel_upcoming_trips_table add column cityCode text null";
        this.P = "create table if not exists user_preferences_flights_table( _id integer primary key autoincrement,funnel_type text not null, email_id text not null,user_data text not null)";
        this.Q = "DROP TABLE IF EXISTS user_preferences_flights_table";
        this.R = "create table my_trips_flight (booking_id text not null,flight_data text not null,pnr_number text,boarding_date long not null,booking_date long not null,amount_paid double not null,currency_code text,primary_contact_number text,booking_status text not null)";
        this.S = " create table flight_hotel_mapping_table (flight_airport_code text primary key,expiry_time long not null,city_code text not null,city_name text not null,country_code text not null)";
        this.T = "create table if not exists hotel_listing_events_table (hotel_listing_events text not null)";
        this.U = "create table if not exists hotel_mmr_ques_table(cityCode text primary key,question_list text not null,timestamp long)";
        this.V = "create table if not exists hotel_mmr_prefs_table(cityCode text primary key,selected_tags text)";
        this.W = "DROP TABLE IF EXISTS hotel_mmr_prefs_table";
        this.X = "DROP TABLE IF EXISTS hotel_mmr_ques_table";
        this.Y = "create table hotel_search_history_table (city_code text not null,city_name text not null,check_in_date text not null,check_out_date text not null,hotelId text not null,countryCode text not null,room_stay_qualifier text not null,timestamp long)";
        this.Z = " create table hotel_cross_sell_table (city_code text primary key,widget_viewed_count integer,widget_last_viewed long,hotel_booking_date long,hotel_checkout_date long)";
        this.aa = "create table my_trips_hotel (booking_id text primary key,hotel_data text not null,check_in_date long not null,check_out_date long not null,booking_date long not null,amount_paid double not null,currency_code text,primary_contact_number text,booking_status text not null)";
        this.ab = "create table my_trips (_id integer primary key autoincrement, booking_id text,boarding_date long not null,table_name text not null,from_city text,to_city text,pnr_number text,hotel_name text,hotel_address text,booking_status text not null,segment_status text)";
        this.ac = " create table images (image_key text primary key,image_data blob not null)";
        this.ad = "CREATE TABLE IF NOT EXISTS bus_destination (city_id int ,city_name text not null,city_mmt_code text not null,city_tvc_code text,city_type long not null)";
        this.ae = "create table hotel_popular_default_table(_id integer primary key autoincrement, city_id text not null, cityCode text not null, countryCode text  not null, city_name text not null, countryName text  not null, latitude text , longitude text, northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text, popularType text, isDefaultCity text not null )";
        this.af = "create table hotel_recent_search_table(_id integer primary key autoincrement, hotelId text , hotel_name text , city_id text, cityCode text, countryCode text not null, city_name text, countryName text, latitude text, longitude text , northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text , type text , crdt date not null)";
        this.ag = "create table if not exists hotel_landing_recent_search_table(_id integer primary key autoincrement, hotelSearchRequest text not null, check_in_date integer not null, crdt date not null)";
        this.ah = "DROP TABLE IF EXISTS my_trips";
        this.ai = "DROP TABLE IF EXISTS my_trips_flight";
        this.aj = "DROP TABLE IF EXISTS my_trips_hotel";
        this.ak = "DROP TABLE IF EXISTS images";
        this.al = "DROP TABLE IF EXISTS bus_destination";
        this.am = "DROP TABLE IF EXISTS bus_save_selection_table";
        this.an = "DROP TABLE IF EXISTS bus_city_table";
        this.ao = "DROP TABLE IF EXISTS hotel_popular_default_table";
        this.ap = "DROP TABLE IF EXISTS hotel_recent_search_table";
        this.aq = "create table upcoming_trips_table(_id integer primary key autoincrement, value text)";
        this.ar = "create table hotel_upcoming_trips_table(_id integer primary key autoincrement, booking_id text , check_in_date long not null,check_out_date long not null,hotelId text , hotel_name text , cityCode text null , city_name text, latitude text, longitude text , hotel_address text , email_id text , first_name text , last_name text , phoneNumber text ,hotel_phoneno text ,hotel_valueplus int ,hotel_image_url text , room_type_id text ,no_ofRooms int ,countryCode text,adult_count int ,checkin_review_submitted int ,checkout_review_submitted int)";
        this.as = "DROP TABLE IF EXISTS user_detail";
        this.at = "DROP TABLE IF EXISTS holidays_image_cache";
        this.au = "create table holidays_traveller_table( _id integer primary key autoincrement,first_name text not null, middle_name text, last_name text not null,pax_type text not null,gender text not null,age integer,title text,nationality text, nationality_code text, dob long, passport_no text, passport_issue_country text, passport_issue_country_code text, passport_expiry_date long, meal_pref text)";
        this.av = "DROP TABLE IF EXISTS holidays_traveller_table";
        this.aw = "alter table holidays_traveller_table drop column age text null";
        this.ax = "alter table holidays_traveller_table add column nationality text null";
        this.ay = "alter table holidays_traveller_table add column nationality_code text null";
        this.az = "alter table holidays_traveller_table add column dob long null";
        this.aA = "alter table holidays_traveller_table add column passport_no text null";
        this.aB = "alter table holidays_traveller_table add column passport_issue_country text null";
        this.aC = "alter table holidays_traveller_table add column passport_issue_country_code text null";
        this.aD = "alter table holidays_traveller_table add column passport_expiry_date long null";
        this.aE = "alter table holidays_traveller_table add column meal_pref text null";
        this.aF = "DROP TABLE IF EXISTS event_log";
        this.aG = "create table if not exists co_traveller_table( _id integer primary key autoincrement,title text not null, travellerId integer not null,first_name text not null, last_name text not null,pax_type text not null,gender text not null,age integer,date_of_birth long not null,email text,meal_pref text,travel_documents text,is_corporate integer DEFAULT 0)";
        this.aH = "create table if not exists country_table( _id integer primary key autoincrement,country_name text not null, country_id text not null)";
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Patch patch = HanselCrashReporter.getPatch(a.class, "onCreate", SQLiteDatabase.class);
        if (patch == null) {
            try {
                sQLiteDatabase.execSQL("create table if not exists get_config_table( _id integer primary key autoincrement,get_config_key text not null, get_config_value text not null)");
                sQLiteDatabase.execSQL("create table my_trips_flight (booking_id text not null,flight_data text not null,pnr_number text,boarding_date long not null,booking_date long not null,amount_paid double not null,currency_code text,primary_contact_number text,booking_status text not null)");
                sQLiteDatabase.execSQL("create table my_trips_hotel (booking_id text primary key,hotel_data text not null,check_in_date long not null,check_out_date long not null,booking_date long not null,amount_paid double not null,currency_code text,primary_contact_number text,booking_status text not null)");
                sQLiteDatabase.execSQL("create table my_trips (_id integer primary key autoincrement, booking_id text,boarding_date long not null,table_name text not null,from_city text,to_city text,pnr_number text,hotel_name text,hotel_address text,booking_status text not null,segment_status text)");
                sQLiteDatabase.execSQL(" create table flight_hotel_mapping_table (flight_airport_code text primary key,expiry_time long not null,city_code text not null,city_name text not null,country_code text not null)");
                sQLiteDatabase.execSQL(" create table hotel_cross_sell_table (city_code text primary key,widget_viewed_count integer,widget_last_viewed long,hotel_booking_date long,hotel_checkout_date long)");
                sQLiteDatabase.execSQL(" create table images (image_key text primary key,image_data blob not null)");
                sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS bus_destination (city_id int ,city_name text not null,city_mmt_code text not null,city_tvc_code text,city_type long not null)");
                sQLiteDatabase.execSQL("create table bus_city_table(_id integer primary key autoincrement, fromCityName text not null, fromCityMmtCode text not null, fromCityTvcCode text not null, toCityName text not null, toCityMmtCode text not null, toCityTvcCode text not null, date text not null, numOfTraveler Number, lastSearchTime Number, UNIQUE (fromCityName,fromCityMmtCode, fromCityTvcCode,toCityName,toCityMmtCode, toCityTvcCode, date, numOfTraveler))");
                sQLiteDatabase.execSQL("create table bus_save_selection_table (_id integer primary key autoincrement, seatNo text not null, fromCityMmtCode text not null, fromCityName text not null, toCityName text not null, tripId text not null, toCityMmtCode text not null, date text not null, bpName text not null, grName text not null, numOfTraveler Number, arrival_time text not null )");
                sQLiteDatabase.execSQL("create table hotel_popular_default_table(_id integer primary key autoincrement, city_id text not null, cityCode text not null, countryCode text  not null, city_name text not null, countryName text  not null, latitude text , longitude text, northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text, popularType text, isDefaultCity text not null )");
                sQLiteDatabase.execSQL("create table hotel_recent_search_table(_id integer primary key autoincrement, hotelId text , hotel_name text , city_id text, cityCode text, countryCode text not null, city_name text, countryName text, latitude text, longitude text , northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text , type text , crdt date not null)");
                sQLiteDatabase.execSQL("create table if not exists hotel_landing_recent_search_table(_id integer primary key autoincrement, hotelSearchRequest text not null, check_in_date integer not null, crdt date not null)");
                sQLiteDatabase.execSQL("create table if not exists hotel_listing_events_table (hotel_listing_events text not null)");
                sQLiteDatabase.execSQL("create table hotel_search_history_table (city_code text not null,city_name text not null,check_in_date text not null,check_out_date text not null,hotelId text not null,countryCode text not null,room_stay_qualifier text not null,timestamp long)");
                sQLiteDatabase.execSQL("create table my_trips_rail (booking_id text not null,booking_date long not null,currency_code text,rail_name text,rail_num text,from_station_code text,arrival_station_code text,arrival_station_name text,boarding_date long,arrival_date long,boarding_point text,pnr_details text,last_update_time_elapse long,boarding_point_code text,journey_duration text,passengers text,primary_contact_number text not null,amount_paid double not null,booking_status text not null,from_city text,to_city text,pnr_number text,fromCityName text,toCityName text)");
                sQLiteDatabase.execSQL("create table city_airport_data_flights_table( _id integer primary key autoincrement,id_data int not null, code text not null,city_name text,country_name text not null,synonyms text not null,description text not null,airport_data text not null,city_type text not null,mapping_type text not null)");
                sQLiteDatabase.execSQL("create table city_mapping_flights_table( _id integer primary key autoincrement,id1_data INTEGER,id2_data int not null,mapping_type text not null)");
                sQLiteDatabase.execSQL("create table flight_service_table( _id integer primary key autoincrement,key text not null,value text not null)");
                sQLiteDatabase.execSQL("create table if not exists flight_updater_table( _id integer primary key autoincrement,key text not null,value LONG not null)");
                sQLiteDatabase.execSQL("create table if not exists flight_dyn_key_val_table( _id integer primary key autoincrement,key text not null,value text not null)");
                sQLiteDatabase.execSQL("create table favourite_search_flights_table(_id integer primary key autoincrement, from_city_code TEXT not null, to_city_code TEXT not null, adult_count INTEGER not null, child_count INTEGER not null, infant_count int not null, departure_date LONG not null, return_date LONG not null, class text not null,trip_type TEXT not null,time_of_search LONG not null )");
                sQLiteDatabase.execSQL("create table if not exists recent_search_flights_table(_id integer primary key autoincrement, rs_old_fare INTEGER not null, rs_new_fare INTEGER not null, rs_seats_avail INTEGER not null, rs_time_of_search LONG not null, rs_fs_id INTEGER not null, FOREIGN KEY (rs_fs_id) REFERENCES favourite_search_flights_table(_id) ON DELETE CASCADE);");
                sQLiteDatabase.execSQL("create table airline_logo_flights_table( airline_code_airline_logo text primary key, airline_logo blob not null)");
                sQLiteDatabase.execSQL("create table airline_code_name_table( airline_code text primary key, airline_name text not null)");
                sQLiteDatabase.execSQL("create table traveller_flights_table( _id integer primary key autoincrement,first_name text not null, last_name text ,pax_type text not null,gender text not null,age integer,title text not null,nationality text null,nationality_code text null,dob long null,passport_no text null,passport_issue_country text null,passport_issue_country_code text null,passport_expiry_date long null,locally_added integer null,pax_id integer)");
                sQLiteDatabase.execSQL("create table if not exists user_preferences_flights_table( _id integer primary key autoincrement,funnel_type text not null, email_id text not null,user_data text not null)");
                sQLiteDatabase.execSQL("create table if not exists user_detail (_id integer primary key autoincrement, affiliate_id text null,affiliate_show_price_pdf text null,company_name text null,child_count integer null,created_by text null,created_date long null,crm_stat text null,address_type text null,email_id text not null,first_name text null,hometown text null,i_agree text null,imint_status text null,imint_tier text null,is_agent text null,last_name text null,middle_name text null,primary_city text null,primary_state text null,landline_number text null,primary_cty text null,primary_house_number text null,primary_postal_cd text null,primary_street text null,primary_address1 text null,primary_address2 text null,status text null,title text null,e_news_letters text null,updated_by text null,profile_type text null,last_updated long null,customer_id text null,age integer null,gender text null,date_of_birth long null,marital_status text null,email_verified text null,mobile_verified text null,mobile_contact_list text null,image_url text null,mmt_auth text null,token text null,login_type text null,is_logged_in integer null,is_corporate integer DEFAULT 0,corp_data text null)");
                sQLiteDatabase.execSQL("create table master_image_cache (cache_image_url text primary key , cache_image_blob blob not null) ");
                sQLiteDatabase.execSQL("create table holidays_image_cache (holidays_cache_image_url text primary key , holidays_cache_image_blob blob not null) ");
                sQLiteDatabase.execSQL("create table if not exists event_log (_id integer primary key autoincrement, event_ud text null,event_sd text null,event_pc text null,event_mg text null,event_ua text null,event_ot text null,event_f1 text null,event_f2 text null,event_f3 text null,event_timestamp Number)");
                sQLiteDatabase.execSQL("create table holidays_traveller_table( _id integer primary key autoincrement,first_name text not null, middle_name text, last_name text not null,pax_type text not null,gender text not null,age integer,title text,nationality text, nationality_code text, dob long, passport_no text, passport_issue_country text, passport_issue_country_code text, passport_expiry_date long, meal_pref text)");
                sQLiteDatabase.execSQL("create table if not exists notification_center (_id integer primary key autoincrement, text text not null,subtext text not null,deepLinkUrl text not null,webPageUrl text not null,campaign text null,image_url text not null,timestamp long not null,type text,data text,read int not null)");
                sQLiteDatabase.execSQL("create table fare_alert_table( _id integer primary key autoincrement,fa_id text                                                                                                                                                                                                                                                                                                                                          not null, fa_msg_count int not null, fa_increment_count int not null, fa_hashcode text)");
                sQLiteDatabase.execSQL("create table if_fare_alert_table( _id integer primary key autoincrement,fa_id text                                                                                                                                                                                                                                                                                                                                          not null, fa_msg_count int not null, fa_increment_count int not null, fa_hashcode text)");
                sQLiteDatabase.execSQL("create table if not exists customer_support_issue_type (_id integer primary key autoincrement, issue_id integer not null, text text not null,subtext text null,rank int null,icon_url text null,type text null,is_show_my_trip int null)");
                sQLiteDatabase.execSQL("create table if not exists customer_support_issue_action (_id integer primary key autoincrement, issue_id integer not null,id integer null,text text not null,subtext text null,rank int null,call_Option int null,write_to_us_Option INTEGER DEFAULT 1,chat_Option int null,icon_url text null,url text null,customer_care_number_set text null)");
                sQLiteDatabase.execSQL("create table if not exists customer_support_lob_trip_type_issue_action (_id integer primary key autoincrement, issue_id integer not null,lob_trip_type text not null,id integer null,text text not null,call_Option int null,write_to_us_Option INTEGER DEFAULT 1,chat_Option int null,customer_care_number_set text null)");
                sQLiteDatabase.execSQL("create table if not exists customer_support_faq (_id integer primary key autoincrement, issue_id integer not null,question text not null,subtext text not null,cta1_url text null,cta1_text text null,cta2_url text null,cta2_text text null)");
                sQLiteDatabase.execSQL("create table if not exists customer_support_reach_us (_id integer primary key autoincrement, customer_care_key integer not null,cc_lob text not null,cc_number text null)");
                sQLiteDatabase.execSQL("create table if not exists customer_improvement_form_info (_id integer primary key autoincrement, issue_id integer not null,releated_to_list text null,issue_list text null)");
                sQLiteDatabase.execSQL("create table if not exists co_traveller_table( _id integer primary key autoincrement,title text not null, travellerId integer not null,first_name text not null, last_name text not null,pax_type text not null,gender text not null,age integer,date_of_birth long not null,email text,meal_pref text,travel_documents text,is_corporate integer DEFAULT 0)");
                sQLiteDatabase.execSQL("create table if not exists country_table( _id integer primary key autoincrement,country_name text not null, country_id text not null)");
                sQLiteDatabase.execSQL("create table if not exists mat_events_timestamp (event_name text primary key, timestamp integer)");
                sQLiteDatabase.execSQL("create table if not exists cache_table (cached_request_key text primary key, cached_response_value blob, cached_response_tag integer, cached_response_code integer, cached_response_encoding text, cached_response_expiry_time integer)");
                sQLiteDatabase.execSQL("create table hotel_upcoming_trips_table(_id integer primary key autoincrement, booking_id text , check_in_date long not null,check_out_date long not null,hotelId text , hotel_name text , cityCode text null , city_name text, latitude text, longitude text , hotel_address text , email_id text , first_name text , last_name text , phoneNumber text ,hotel_phoneno text ,hotel_valueplus int ,hotel_image_url text , room_type_id text ,no_ofRooms int ,countryCode text,adult_count int ,checkin_review_submitted int ,checkout_review_submitted int)");
                sQLiteDatabase.execSQL("create table upcoming_trips_table(_id integer primary key autoincrement, value text)");
                sQLiteDatabase.execSQL("create table if not exists Notification_setting (_id integer primary key autoincrement, identifier text not null, expiryDate LONG not null)");
                sQLiteDatabase.execSQL("create table personalized_home_page_table (key text primary key,response text,request text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)");
                sQLiteDatabase.execSQL("create table if not exists hotel_mmr_ques_table(cityCode text primary key,question_list text not null,timestamp long)");
                sQLiteDatabase.execSQL("create table if not exists hotel_mmr_prefs_table(cityCode text primary key,selected_tags text)");
                sQLiteDatabase.execSQL("create table if not exists holiday_session_data(pkg_id integer primary key, pkg_name text not null, duration text, action text not null, category integer, timestamp long, tag_dest text, branch text, image_path text, pkg_dest_info text, pkg_dynamic integer DEFAULT 0)");
                sQLiteDatabase.execSQL("create table if not exists holiday_recent_destination(dest_name text primary key, dest_branch text, dest_timestamp long)");
                sQLiteDatabase.execSQL("create table if not exists holiday_landing_data(event_key text primary key, response blob)");
                sQLiteDatabase.execSQL("create table if not exists holiday_change_hotel_data (_id integer primary key autoincrement, pkg_id integer not null, dest_city_name text not null, dest_city_id text not null, sequence_no int not null, sequence_id text not null, room_type_code text not null, rate_type_code text not null, timestamp long)");
                sQLiteDatabase.execSQL("create table if not exists flight_last_viewed_table(column_last_viewed_search_key text primary key, column_flights_data text not null, funnel_type text not null);");
                sQLiteDatabase.execSQL("create table personalized_hotel_landing_table (key text primary key,response text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)");
                sQLiteDatabase.execSQL("create table app_config_data (data_id text primary key not null,data_string text)");
                sQLiteDatabase.execSQL("create table hotel_static_persuasions (persuasion_id text primary key not null,persuasion_desc text,persuasion_page_name text,persuasion_placeholder text,persuasion_priority integer default 0)");
                return;
            } catch (SQLException e) {
                e.toString();
                LogUtils.a(e);
                return;
            }
        }
        patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase}).toPatchJoinPoint());
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        int i3;
        Patch patch = HanselCrashReporter.getPatch(a.class, "onUpgrade", SQLiteDatabase.class, Integer.TYPE, Integer.TYPE);
        if (patch == null) {
            if (i < 5) {
                try {
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS my_trips");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS my_trips_flight");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS my_trips_hotel");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS images");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS bus_destination");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS bus_city_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS bus_save_selection_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_popular_default_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_recent_search_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS city_airport_data_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS city_mapping_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS flight_service_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS flight_updater_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS flight_dyn_key_val_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS favourite_search_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS recent_search_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS airline_logo_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS airline_code_name_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS user_detail");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS traveller_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS user_preferences_flights_table");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS master_image_cache");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS holidays_image_cache");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS event_log");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS holidays_traveller_table");
                } catch (SQLException e) {
                    e.toString();
                    LogUtils.a(e);
                }
                try {
                    sQLiteDatabase.execSQL("create table my_trips_flight (booking_id text not null,flight_data text not null,pnr_number text,boarding_date long not null,booking_date long not null,amount_paid double not null,currency_code text,primary_contact_number text,booking_status text not null)");
                    sQLiteDatabase.execSQL("create table my_trips_hotel (booking_id text primary key,hotel_data text not null,check_in_date long not null,check_out_date long not null,booking_date long not null,amount_paid double not null,currency_code text,primary_contact_number text,booking_status text not null)");
                    sQLiteDatabase.execSQL("create table my_trips (_id integer primary key autoincrement, booking_id text,boarding_date long not null,table_name text not null,from_city text,to_city text,pnr_number text,hotel_name text,hotel_address text,booking_status text not null,segment_status text)");
                    sQLiteDatabase.execSQL(" create table images (image_key text primary key,image_data blob not null)");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS bus_destination (city_id int ,city_name text not null,city_mmt_code text not null,city_tvc_code text,city_type long not null)");
                    sQLiteDatabase.execSQL("create table bus_city_table(_id integer primary key autoincrement, fromCityName text not null, fromCityMmtCode text not null, fromCityTvcCode text not null, toCityName text not null, toCityMmtCode text not null, toCityTvcCode text not null, date text not null, numOfTraveler Number, lastSearchTime Number, UNIQUE (fromCityName,fromCityMmtCode, fromCityTvcCode,toCityName,toCityMmtCode, toCityTvcCode, date, numOfTraveler))");
                    sQLiteDatabase.execSQL("create table bus_save_selection_table (_id integer primary key autoincrement, seatNo text not null, fromCityMmtCode text not null, fromCityName text not null, toCityName text not null, tripId text not null, toCityMmtCode text not null, date text not null, bpName text not null, grName text not null, numOfTraveler Number, arrival_time text not null )");
                    sQLiteDatabase.execSQL("create table hotel_popular_default_table(_id integer primary key autoincrement, city_id text not null, cityCode text not null, countryCode text  not null, city_name text not null, countryName text  not null, latitude text , longitude text, northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text, popularType text, isDefaultCity text not null )");
                    sQLiteDatabase.execSQL("create table hotel_recent_search_table(_id integer primary key autoincrement, hotelId text , hotel_name text , city_id text, cityCode text, countryCode text not null, city_name text, countryName text, latitude text, longitude text , northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text , type text , crdt date not null)");
                    sQLiteDatabase.execSQL("create table city_airport_data_flights_table( _id integer primary key autoincrement,id_data int not null, code text not null,city_name text,country_name text not null,synonyms text not null,description text not null,airport_data text not null,city_type text not null,mapping_type text not null)");
                    sQLiteDatabase.execSQL("create table city_mapping_flights_table( _id integer primary key autoincrement,id1_data INTEGER,id2_data int not null,mapping_type text not null)");
                    sQLiteDatabase.execSQL("create table flight_service_table( _id integer primary key autoincrement,key text not null,value text not null)");
                    sQLiteDatabase.execSQL("create table if not exists flight_updater_table( _id integer primary key autoincrement,key text not null,value LONG not null)");
                    sQLiteDatabase.execSQL("create table if not exists flight_dyn_key_val_table( _id integer primary key autoincrement,key text not null,value text not null)");
                    sQLiteDatabase.execSQL("create table favourite_search_flights_table(_id integer primary key autoincrement, from_city_code TEXT not null, to_city_code TEXT not null, adult_count INTEGER not null, child_count INTEGER not null, infant_count int not null, departure_date LONG not null, return_date LONG not null, class text not null,trip_type TEXT not null,time_of_search LONG not null )");
                    sQLiteDatabase.execSQL("create table airline_logo_flights_table( airline_code_airline_logo text primary key, airline_logo blob not null)");
                    sQLiteDatabase.execSQL("create table airline_code_name_table( airline_code text primary key, airline_name text not null)");
                    sQLiteDatabase.execSQL("create table if not exists user_detail (_id integer primary key autoincrement, affiliate_id text null,affiliate_show_price_pdf text null,company_name text null,child_count integer null,created_by text null,created_date long null,crm_stat text null,address_type text null,email_id text not null,first_name text null,hometown text null,i_agree text null,imint_status text null,imint_tier text null,is_agent text null,last_name text null,middle_name text null,primary_city text null,primary_state text null,landline_number text null,primary_cty text null,primary_house_number text null,primary_postal_cd text null,primary_street text null,primary_address1 text null,primary_address2 text null,status text null,title text null,e_news_letters text null,updated_by text null,profile_type text null,last_updated long null,customer_id text null,age integer null,gender text null,date_of_birth long null,marital_status text null,email_verified text null,mobile_verified text null,mobile_contact_list text null,image_url text null,mmt_auth text null,token text null,login_type text null,is_logged_in integer null,is_corporate integer DEFAULT 0,corp_data text null)");
                    sQLiteDatabase.execSQL("create table traveller_flights_table( _id integer primary key autoincrement,first_name text not null, last_name text ,pax_type text not null,gender text not null,age integer,title text not null,nationality text null,nationality_code text null,dob long null,passport_no text null,passport_issue_country text null,passport_issue_country_code text null,passport_expiry_date long null,locally_added integer null,pax_id integer)");
                    sQLiteDatabase.execSQL("create table if not exists user_preferences_flights_table( _id integer primary key autoincrement,funnel_type text not null, email_id text not null,user_data text not null)");
                    sQLiteDatabase.execSQL("create table master_image_cache (cache_image_url text primary key , cache_image_blob blob not null) ");
                    sQLiteDatabase.execSQL("create table holidays_image_cache (holidays_cache_image_url text primary key , holidays_cache_image_blob blob not null) ");
                    sQLiteDatabase.execSQL("create table if not exists event_log (_id integer primary key autoincrement, event_ud text null,event_sd text null,event_pc text null,event_mg text null,event_ua text null,event_ot text null,event_f1 text null,event_f2 text null,event_f3 text null,event_timestamp Number)");
                } catch (SQLException e2) {
                    e2.toString();
                    LogUtils.a(e2);
                }
                i3 = 5;
            } else {
                i3 = i;
            }
            if (i3 != i2) {
                if (i3 == 5) {
                    try {
                        sQLiteDatabase.execSQL("create table if not exists flight_dyn_key_val_table( _id integer primary key autoincrement,key text not null,value text not null)");
                        sQLiteDatabase.execSQL("create table if not exists flight_updater_table( _id integer primary key autoincrement,key text not null,value LONG not null)");
                        sQLiteDatabase.execSQL("create table if not exists user_preferences_flights_table( _id integer primary key autoincrement,funnel_type text not null, email_id text not null,user_data text not null)");
                        sQLiteDatabase.execSQL("create table if not exists event_log (_id integer primary key autoincrement, event_ud text null,event_sd text null,event_pc text null,event_mg text null,event_ua text null,event_ot text null,event_f1 text null,event_f2 text null,event_f3 text null,event_timestamp Number)");
                    } catch (SQLException e3) {
                        e3.toString();
                        LogUtils.a(e3);
                    }
                    i3 = 6;
                }
                if (i3 != i2) {
                    if (i3 == 6) {
                        try {
                            sQLiteDatabase.execSQL("create table my_trips_rail (booking_id text not null,booking_date long not null,currency_code text,rail_name text,rail_num text,from_station_code text,arrival_station_code text,arrival_station_name text,boarding_date long,arrival_date long,boarding_point text,pnr_details text,last_update_time_elapse long,boarding_point_code text,journey_duration text,passengers text,primary_contact_number text not null,amount_paid double not null,booking_status text not null,from_city text,to_city text,pnr_number text,fromCityName text,toCityName text)");
                        } catch (SQLException e4) {
                            e4.toString();
                            LogUtils.a(e4);
                        }
                        i3 = 7;
                    }
                    if (i3 != i2) {
                        if (i3 == 7) {
                            try {
                                sQLiteDatabase.execSQL("create table if not exists recent_search_flights_table(_id integer primary key autoincrement, rs_old_fare INTEGER not null, rs_new_fare INTEGER not null, rs_seats_avail INTEGER not null, rs_time_of_search LONG not null, rs_fs_id INTEGER not null, FOREIGN KEY (rs_fs_id) REFERENCES favourite_search_flights_table(_id) ON DELETE CASCADE);");
                            } catch (SQLException e5) {
                                e5.toString();
                                LogUtils.a(e5);
                            }
                            i3 = 8;
                        }
                        if (i3 != i2) {
                            if (i3 == 8) {
                                try {
                                    sQLiteDatabase.execSQL("create table holidays_traveller_table( _id integer primary key autoincrement,first_name text not null, middle_name text, last_name text not null,pax_type text not null,gender text not null,age integer,title text,nationality text, nationality_code text, dob long, passport_no text, passport_issue_country text, passport_issue_country_code text, passport_expiry_date long, meal_pref text)");
                                    sQLiteDatabase.execSQL("create table if not exists notification_center (_id integer primary key autoincrement, text text not null,subtext text not null,deepLinkUrl text not null,webPageUrl text not null,campaign text null,image_url text not null,timestamp long not null,type text,data text,read int not null)");
                                } catch (SQLException e6) {
                                    e6.toString();
                                    LogUtils.a(e6);
                                }
                                i3 = 9;
                            }
                            if (i3 == 9) {
                                try {
                                    sQLiteDatabase.execSQL("create table if not exists customer_support_issue_type (_id integer primary key autoincrement, issue_id integer not null, text text not null,subtext text null,rank int null,icon_url text null,type text null,is_show_my_trip int null)");
                                    sQLiteDatabase.execSQL("create table if not exists customer_support_issue_action (_id integer primary key autoincrement, issue_id integer not null,id integer null,text text not null,subtext text null,rank int null,call_Option int null,write_to_us_Option INTEGER DEFAULT 1,chat_Option int null,icon_url text null,url text null,customer_care_number_set text null)");
                                    sQLiteDatabase.execSQL("create table if not exists customer_support_faq (_id integer primary key autoincrement, issue_id integer not null,question text not null,subtext text not null,cta1_url text null,cta1_text text null,cta2_url text null,cta2_text text null)");
                                } catch (SQLException e7) {
                                    e7.toString();
                                    LogUtils.a(e7);
                                }
                                i3 = 10;
                            }
                            if (i3 == 10) {
                                try {
                                    sQLiteDatabase.execSQL("create table fare_alert_table( _id integer primary key autoincrement,fa_id text                                                                                                                                                                                                                                                                                                                                          not null, fa_msg_count int not null, fa_increment_count int not null, fa_hashcode text)");
                                    sQLiteDatabase.execSQL("alter table customer_support_issue_type add column type text null");
                                    sQLiteDatabase.execSQL("alter table customer_support_issue_type add column is_show_my_trip int null");
                                    sQLiteDatabase.execSQL("alter table customer_support_issue_action add column customer_care_number_set text null");
                                    sQLiteDatabase.execSQL("alter table customer_support_issue_action add column id text null");
                                    sQLiteDatabase.execSQL("create table if not exists customer_support_reach_us (_id integer primary key autoincrement, customer_care_key integer not null,cc_lob text not null,cc_number text null)");
                                    sQLiteDatabase.execSQL("create table if not exists customer_improvement_form_info (_id integer primary key autoincrement, issue_id integer not null,releated_to_list text null,issue_list text null)");
                                } catch (SQLException e8) {
                                    e8.toString();
                                    LogUtils.a(e8);
                                }
                                i3 = 11;
                            }
                            if (i3 != i2) {
                                if (i3 == 11) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists mat_events_timestamp (event_name text primary key, timestamp integer)");
                                        sQLiteDatabase.execSQL("create table if not exists co_traveller_table( _id integer primary key autoincrement,title text not null, travellerId integer not null,first_name text not null, last_name text not null,pax_type text not null,gender text not null,age integer,date_of_birth long not null,email text,meal_pref text,travel_documents text,is_corporate integer DEFAULT 0)");
                                        sQLiteDatabase.execSQL("create table if not exists country_table( _id integer primary key autoincrement,country_name text not null, country_id text not null)");
                                    } catch (SQLException e9) {
                                        e9.toString();
                                        LogUtils.a(e9);
                                    }
                                    i3 = 12;
                                }
                                if (i3 == 12) {
                                    try {
                                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_popular_default_table");
                                        sQLiteDatabase.execSQL("create table hotel_popular_default_table(_id integer primary key autoincrement, city_id text not null, cityCode text not null, countryCode text  not null, city_name text not null, countryName text  not null, latitude text , longitude text, northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text, popularType text, isDefaultCity text not null )");
                                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_recent_search_table");
                                        sQLiteDatabase.execSQL("create table hotel_recent_search_table(_id integer primary key autoincrement, hotelId text , hotel_name text , city_id text, cityCode text, countryCode text not null, city_name text, countryName text, latitude text, longitude text , northEastLatitude text, northEastLongitude text, southWestLatitude text, southWestLongitude text , suggest_id text , type text , crdt date not null)");
                                        sQLiteDatabase.execSQL("create table if not exists cache_table (cached_request_key text primary key, cached_response_value blob, cached_response_tag integer, cached_response_code integer, cached_response_encoding text, cached_response_expiry_time integer)");
                                    } catch (SQLException e10) {
                                        e10.toString();
                                        LogUtils.a(e10);
                                    }
                                    i3 = 13;
                                }
                                if (i3 == 13) {
                                    try {
                                        sQLiteDatabase.execSQL("create table hotel_upcoming_trips_table(_id integer primary key autoincrement, booking_id text , check_in_date long not null,check_out_date long not null,hotelId text , hotel_name text , cityCode text null , city_name text, latitude text, longitude text , hotel_address text , email_id text , first_name text , last_name text , phoneNumber text ,hotel_phoneno text ,hotel_valueplus int ,hotel_image_url text , room_type_id text ,no_ofRooms int ,countryCode text,adult_count int ,checkin_review_submitted int ,checkout_review_submitted int)");
                                        sQLiteDatabase.execSQL("create table upcoming_trips_table(_id integer primary key autoincrement, value text)");
                                    } catch (SQLException e11) {
                                        LogUtils.b(e11);
                                    }
                                    i3 = 14;
                                }
                                if (i3 == 14) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if_fare_alert_table( _id integer primary key autoincrement,fa_id text                                                                                                                                                                                                                                                                                                                                          not null, fa_msg_count int not null, fa_increment_count int not null, fa_hashcode text)");
                                    } catch (SQLException e12) {
                                        LogUtils.b(e12);
                                    }
                                    i3 = 15;
                                }
                                if (i3 == 15) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table customer_support_issue_action add column write_to_us_Option INTEGER DEFAULT 1");
                                    } catch (SQLException e13) {
                                        e13.toString();
                                        LogUtils.a(e13);
                                    }
                                    i3 = 16;
                                }
                                if (i3 == 16) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table notification_center add column type text");
                                        sQLiteDatabase.execSQL("alter table notification_center add column data text");
                                    } catch (SQLException e14) {
                                        e14.toString();
                                        LogUtils.a(e14);
                                    }
                                    i3 = 17;
                                }
                                if (i3 == 17) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists customer_support_lob_trip_type_issue_action (_id integer primary key autoincrement, issue_id integer not null,lob_trip_type text not null,id integer null,text text not null,call_Option int null,write_to_us_Option INTEGER DEFAULT 1,chat_Option int null,customer_care_number_set text null)");
                                        sQLiteDatabase.execSQL(" create table flight_hotel_mapping_table (flight_airport_code text primary key,expiry_time long not null,city_code text not null,city_name text not null,country_code text not null)");
                                    } catch (SQLException e15) {
                                        e15.toString();
                                        LogUtils.a(e15);
                                    }
                                    i3 = 18;
                                }
                                if (i3 == 18) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column nationality text null");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column nationality_code text null");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column dob long null");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column passport_no text null");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column passport_issue_country text null");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column passport_issue_country_code text null");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column passport_expiry_date long null");
                                        sQLiteDatabase.execSQL(" create table hotel_cross_sell_table (city_code text primary key,widget_viewed_count integer,widget_last_viewed long,hotel_booking_date long,hotel_checkout_date long)");
                                    } catch (SQLException e16) {
                                        e16.toString();
                                        LogUtils.a(e16);
                                    }
                                    i3 = 19;
                                }
                                if (i3 == 19) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists hotel_listing_events_table (hotel_listing_events text not null)");
                                        sQLiteDatabase.execSQL("create table if not exists Notification_setting (_id integer primary key autoincrement, identifier text not null, expiryDate LONG not null)");
                                    } catch (SQLException e17) {
                                        e17.toString();
                                        LogUtils.a(e17);
                                    }
                                    i3 = 20;
                                }
                                if (i3 == 20) {
                                    try {
                                        sQLiteDatabase.execSQL("create table hotel_search_history_table (city_code text not null,city_name text not null,check_in_date text not null,check_out_date text not null,hotelId text not null,countryCode text not null,room_stay_qualifier text not null,timestamp long)");
                                    } catch (SQLException e18) {
                                        e18.toString();
                                        LogUtils.a(e18);
                                    }
                                    i3 = 21;
                                }
                                if (i3 == 21) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists get_config_table( _id integer primary key autoincrement,get_config_key text not null, get_config_value text not null)");
                                        sQLiteDatabase.execSQL("create table if not exists hotel_mmr_ques_table(cityCode text primary key,question_list text not null,timestamp long)");
                                        sQLiteDatabase.execSQL("create table if not exists hotel_mmr_prefs_table(cityCode text primary key,selected_tags text)");
                                    } catch (SQLException e19) {
                                        e19.toString();
                                        LogUtils.a(e19);
                                    }
                                    i3 = 22;
                                }
                                if (i3 == 22) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column locally_added integer null");
                                    } catch (SQLException e20) {
                                        e20.getMessage();
                                        LogUtils.a(e20);
                                    }
                                    i3 = 23;
                                }
                                if (i3 == 23) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table hotel_upcoming_trips_table add column cityCode text null");
                                    } catch (SQLException e21) {
                                        e21.getMessage();
                                        LogUtils.a(e21);
                                    }
                                    i3 = 24;
                                }
                                if (i3 == 24) {
                                    try {
                                        sQLiteDatabase.execSQL("create table personalized_home_page_table (key text primary key,response text,request text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)");
                                    } catch (SQLException e22) {
                                        e22.toString();
                                        LogUtils.a(e22);
                                    }
                                    i3 = 25;
                                }
                                if (i3 == 25) {
                                    try {
                                        sQLiteDatabase.execSQL("create table personalized_home_page_table (key text primary key,response text,request text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)");
                                    } catch (SQLException e23) {
                                        e23.toString();
                                        LogUtils.a(e23);
                                    }
                                    i3 = 26;
                                }
                                if (i3 == 26) {
                                    try {
                                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_mmr_prefs_table");
                                        sQLiteDatabase.execSQL("create table if not exists hotel_mmr_prefs_table(cityCode text primary key,selected_tags text)");
                                    } catch (SQLException e24) {
                                        e24.toString();
                                        LogUtils.a(e24);
                                    }
                                    i3 = 27;
                                }
                                if (i3 == 27) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table co_traveller_table add column is_corporate integer DEFAULT 0");
                                        sQLiteDatabase.execSQL("alter table user_detail add column is_corporate integer DEFAULT 0");
                                        sQLiteDatabase.execSQL("alter table user_detail add column corp_data text null");
                                        sQLiteDatabase.execSQL("alter table personalized_home_page_table add column is_corporate integer DEFAULT 0");
                                    } catch (SQLException e25) {
                                        e25.getMessage();
                                        LogUtils.a(e25);
                                    }
                                    i3 = 28;
                                }
                                if (i3 == 28) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists holiday_session_data(pkg_id integer primary key, pkg_name text not null, duration text, action text not null, category integer, timestamp long, tag_dest text, branch text, image_path text, pkg_dest_info text, pkg_dynamic integer DEFAULT 0)");
                                        sQLiteDatabase.execSQL("create table if not exists holiday_recent_destination(dest_name text primary key, dest_branch text, dest_timestamp long)");
                                        sQLiteDatabase.execSQL("create table if not exists holiday_landing_data(event_key text primary key, response blob)");
                                        sQLiteDatabase.execSQL("alter table traveller_flights_table add column pax_id integer");
                                    } catch (SQLException e26) {
                                        e26.getMessage();
                                        LogUtils.a(e26);
                                    }
                                    i3 = 29;
                                }
                                if (i3 == 29) {
                                    try {
                                        sQLiteDatabase.execSQL("alter table user_detail add column mobile_verified text null");
                                    } catch (SQLException e27) {
                                        e27.getMessage();
                                        LogUtils.a(e27);
                                    }
                                    i3 = 30;
                                }
                                if (i3 == 30) {
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists flight_last_viewed_table(column_last_viewed_search_key text primary key, column_flights_data text not null, funnel_type text not null);");
                                        sQLiteDatabase.execSQL("alter table user_detail add column mobile_contact_list text null");
                                        sQLiteDatabase.execSQL("create table if not exists holiday_change_hotel_data (_id integer primary key autoincrement, pkg_id integer not null, dest_city_name text not null, dest_city_id text not null, sequence_no int not null, sequence_id text not null, room_type_code text not null, rate_type_code text not null, timestamp long)");
                                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_mmr_ques_table");
                                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS hotel_mmr_prefs_table");
                                        sQLiteDatabase.execSQL("create table if not exists hotel_mmr_prefs_table(cityCode text primary key,selected_tags text)");
                                        sQLiteDatabase.execSQL("alter table holiday_session_data add column pkg_dest_info text null");
                                        sQLiteDatabase.execSQL("alter table holiday_session_data add column pkg_dynamic integer DEFAULT 0");
                                    } catch (SQLException e28) {
                                        e28.getMessage();
                                        LogUtils.a(e28);
                                    }
                                    try {
                                        sQLiteDatabase.execSQL("alter table user_detail add column corp_data text null");
                                        at.b().b("loggedIn_user_MMT_Auth", (String) null);
                                    } catch (Throwable th) {
                                    }
                                    try {
                                        sQLiteDatabase.execSQL("alter table user_detail add column is_corporate integer DEFAULT 0");
                                        at.b().b("loggedIn_user_MMT_Auth", (String) null);
                                    } catch (Throwable th2) {
                                    }
                                    i3 = 31;
                                }
                                if (i3 == 31) {
                                    try {
                                        sQLiteDatabase.execSQL("create table personalized_hotel_landing_table (key text primary key,response text,dataKey text,timestamp long, is_corporate integer DEFAULT 0)");
                                        sQLiteDatabase.execSQL("ALTER TABLE flight_last_viewed_table ADD funnel_type text");
                                        sQLiteDatabase.execSQL("update flight_last_viewed_table set funnel_type = 'DOM'");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column nationality text null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column nationality_code text null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column passport_no text null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column passport_expiry_date long null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column passport_issue_country text null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column passport_issue_country_code text null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column dob long null");
                                        sQLiteDatabase.execSQL("alter table holidays_traveller_table add column meal_pref text null");
                                    } catch (SQLException e29) {
                                        e29.getMessage();
                                        LogUtils.a(e29);
                                    }
                                    i3 = 32;
                                }
                                if (i3 == 32) {
                                    try {
                                        sQLiteDatabase.execSQL("create table app_config_data (data_id text primary key not null,data_string text)");
                                        sQLiteDatabase.execSQL("create table hotel_static_persuasions (persuasion_id text primary key not null,persuasion_desc text,persuasion_page_name text,persuasion_placeholder text,persuasion_priority integer default 0)");
                                    } catch (SQLException e30) {
                                        e30.getMessage();
                                        LogUtils.a(e30);
                                    }
                                    i3 = 33;
                                }
                                if (i3 == 33) {
                                    try {
                                        h.k();
                                        h.l();
                                        h.j();
                                    } catch (Exception e31) {
                                        LogUtils.a(new Exception("error when deleting data in onUpgrade"));
                                    }
                                    try {
                                        sQLiteDatabase.execSQL("create table if not exists hotel_landing_recent_search_table(_id integer primary key autoincrement, hotelSearchRequest text not null, check_in_date integer not null, crdt date not null)");
                                    } catch (SQLException e32) {
                                        e32.getMessage();
                                        LogUtils.a(e32);
                                    }
                                    i3 = 34;
                                }
                                if (i3 == i2) {
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase, new Integer(i), new Integer(i2)}).toPatchJoinPoint());
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onOpen(SQLiteDatabase sQLiteDatabase) {
        Patch patch = HanselCrashReporter.getPatch(a.class, "onOpen", SQLiteDatabase.class);
        if (patch == null) {
            super.onOpen(sQLiteDatabase);
            sQLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
        } else {
            patch.apply(new PatchJoinPoint.PatchJoinPointBuilder().setClassOfMethod(patch.getClassForPatch()).setMethod(patch.getMethodForPatch()).setTarget(this).setArguments(new Object[]{sQLiteDatabase}).toPatchJoinPoint());
        }
    }
}
