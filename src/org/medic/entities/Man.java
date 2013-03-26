package org.medic.entities;

import java.util.Calendar;

public class Man
{
    // Адрес проживания
    LiveAddress mAddress;
    LiveAddress mWorkAddress;
    // Фамилия
    String lastName;
    // Имя
    String firstName;
    // Отчество
    String middleName;
    // Пол
    String gender;
    // Дата рождения
    Calendar birthDate;
    // Льготная группа
    String insuranceGroup;
    // Номер льготного удостоверения
    String insuranceNumber;
    // Сигнальные отметки
    String signalMarks;
    // Лист диагнозов
    String finalDiagnosisSheet;
    // ведомости о прививках
    String vaccinationInfo;
    // сроки временной нетрудоспособности
    String illDates;
    // Информация о госпитализации
    String hospitalInfo;
    // Дневник больного
    String illDiary;
    // Ежегодный эпикризис
    String yearlyEpicrisis;
    // План наблюдения на следующий год
    String nextYearPlan;

     // Полное имя
    public String getFullName()
    {
        return lastName + " " + firstName + " " + middleName;
    }
}
