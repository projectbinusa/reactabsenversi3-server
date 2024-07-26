import {
  faAddressCard,
  faArrowRightFromBracket,
  faBriefcase,
  faBuilding,
  faBusinessTime,
  faCalendar,
  faCalendarDay,
  faCalendarDays,
  faCalendarWeek,
  faChalkboardUser,
  faChevronDown,
  faChevronUp,
  faClock,
  faCube,
  faDatabase,
  faKey,
  faMapLocationDot,
  faSignal,
  faTable,
  faUserCheck,
  faUserGear,
  faUserPen,
  faUserPlus,
  faUserTie,
  faUsers,
  faUsersGear,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useState } from "react";
import Swal from "sweetalert2";

const Sidebar = () => {
  const role = localStorage.getItem("role");
  // State untuk mengelola status dropdown
  const [masterDataOpen, setMasterDataOpen] = useState(false);
  const [rekapanOpen, setRekapanOpen] = useState(false);
  const [absenOpen, setAbsenOpen] = useState(false);

  // Fungsi untuk menampilkan atau menyembunyikan dropdown master data
  const toggleMasterData = () => {
    setMasterDataOpen(!masterDataOpen);
  };

  // Fungsi untuk menampilkan atau menyembunyikan dropdown rekapan
  const toggleRekapan = () => {
    setRekapanOpen(!rekapanOpen);
  };

  // Fungsi untuk menampilkan atau menyembunyikan dropdown absen
  const toggleAbsen = () => {
    setAbsenOpen(!absenOpen);
  };



  return (
    <aside
      id="logo-sidebar"
      className="fixed top-0 left-0 z-40 w-64 h-screen pt-20 transition-transform -translate-x-full bg-white border-r border-blue-200 sm:translate-x-0 dark:bg-blue-800 dark:border-blue-700"
      aria-label="Sidebar"
    >
      <div className="h-full px-3 pb-4 overflow-y-auto bg-white dark:bg-blue-800">
        <ul className="space-y-2 font-medium">
          {role === "ADMIN" && (
            <ul>
              <li>
                <a
                  href="/admin/dashboard"
                  className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faCube}
                  />
                  <span className="ms-3">Dashboard</span>
                </a>
              </li>
              {/* // <!-- Dropdown Master Data --> */}
              <li>
                <button
                  type="button"
                  className="flex items-center w-full p-2 text-base text-blue-900 transition duration-75 rounded-lg group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                  // aria-controls="dropdown-masterdata"
                  // data-dropdown-toggle="dropdown-masterdata"
                  onClick={toggleMasterData}
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faDatabase}
                  />
                  <span className="flex-1 ml-3 text-left whitespace-nowrap">
                    Master Data
                  </span>
                  <FontAwesomeIcon
                    icon={masterDataOpen ? faChevronUp : faChevronDown}
                    className="flex-shrink-0 w-4 h-4 text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                  />
                </button>
                <ul
                  // id="dropdown-masterdata"
                  className={`${
                    masterDataOpen ? "" : "hidden" // Tampilkan atau sembunyikan dropdown berdasarkan state masterDataOpen
                  } py-2 space-y-2`}
                >
                  {/* <!-- Menu Karyawan --> */}
                  <li>
                    <a
                      href="/admin/karyawan"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faUsersGear}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">
                        Karyawan
                      </span>
                    </a>
                  </li>

                  {/* <!-- Menu Jabatan --> */}
                  <li>
                    <a
                      href="/admin/jabatan"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBriefcase}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Jabatan</span>
                    </a>
                  </li>

                  {/* <!-- Menu Jam Kerja --> */}
                  <li>
                    <a
                      href="/admin/shift"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBusinessTime}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Shift</span>
                    </a>
                  </li>

                  {/* <!-- Menu Lokasi --> */}
                  <li>
                    <a
                      href="/admin/lokasi"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faMapLocationDot}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Lokasi</span>
                    </a>
                  </li>

                  {/* <!-- Menu Organisasi --> */}
                  <li>
                    <a
                      href="/admin/organisasi"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBuilding}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">
                        Organisasi
                      </span>
                    </a>
                  </li>
                </ul>
              </li>
              {/* <!-- Dropdown Rekapan --> */}
              <li>
                <button
                  type="button"
                  className="flex items-center w-full p-2 text-base text-blue-900 transition duration-75 rounded-lg group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                  // aria-controls="dropdown-example"
                  // data-collapse-toggle="dropdown-example"
                  onClick={toggleRekapan}
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faSignal}
                  />
                  <span className="flex-1 ml-3 text-left whitespace-nowrap">
                    Rekapan
                  </span>
                  <FontAwesomeIcon
                    icon={rekapanOpen ? faChevronUp : faChevronDown}
                    className="flex-shrink-0 w-4 h-4 text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                  />
                </button>

                <ul
                  // id="dropdown-masterdata"
                  className={`${
                    rekapanOpen ? "" : "hidden" // Tampilkan atau sembunyikan dropdown berdasarkan state masterDataOpen
                  } py-2 space-y-2`}
                >
                  {/* <!-- Menu Simpel --> */}
                  <li>
                    <a
                      href="/admin/simpel"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faUserGear}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Simpel</span>
                    </a>
                  </li>
                  {/* <!-- Menu PerKaryawan --> */}
                  <li>
                    <a
                      href="/admin/perkaryawan"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faUserPen}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">
                        Perkaryawan
                      </span>
                    </a>
                  </li>
                  {/* <!-- Menu Harian --> */}
                  <li>
                    <a
                      href="/admin/harian"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faCalendarDay}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Harian</span>
                    </a>
                  </li>
                  {/* <!-- Menu Mingguan --> */}
                  <li>
                    <a
                      href="/admin/mingguan"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faCalendarWeek}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">
                        Mingguan
                      </span>
                    </a>
                  </li>
                  {/* <!-- Menu Bulanan --> */}
                  <li>
                    <a
                      href="/admin/bulanan"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faCalendar}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Bulanan</span>
                    </a>
                  </li>
                </ul>
              </li>

              {/* // <!-- Dropdown Absen --> */}
              <li>
                <button
                  type="button"
                  className="flex items-center w-full p-2 text-base text-blue-900 transition duration-75 rounded-lg group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                  // aria-controls="dropdown-data"
                  // data-collapse-toggle="dropdown-data"
                  onClick={toggleAbsen}
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faTable}
                  />
                  <span className="flex-1 ml-3 text-left whitespace-nowrap">
                    Data Absensi
                  </span>
                  <FontAwesomeIcon
                    icon={absenOpen ? faChevronUp : faChevronDown}
                    className="flex-shrink-0 w-4 h-4 text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                  />
                </button>

                <ul
                  id="dropdown-masterdata"
                  className={`${
                    absenOpen ? "" : "hidden" // Tampilkan atau sembunyikan dropdown berdasarkan state masterDataOpen
                  } py-2 space-y-2`}
                >
                  {/* <!-- Menu Absensi --> */}
                  <li>
                    <a
                      href="/admin/absensi"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faAddressCard}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Absensi</span>
                    </a>
                  </li>
                  {/* <!-- Menu Cuti --> */}
                  <li>
                    <a
                      href="/admin/cuti"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faCalendarDays}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Cuti</span>
                    </a>
                  </li>
                  {/* <!-- Menu Kehadiran --> */}
                  <li>
                    <a
                      href="/admin/kehadiran"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faUserCheck}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">
                        Kehadiran
                      </span>
                    </a>
                  </li>
                  {/* <!-- Menu Mingguan --> */}
                  <li>
                    <a
                      href="/admin/lembur"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBusinessTime}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Lembur</span>
                    </a>
                  </li>
                </ul>
              </li>
            </ul>
          )}
          {role === "USER" && (
            <ul>
              {" "}
              <li>
                <a
                  href="/user/dashboard"
                  className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faCube}
                  />
                  <span className="ms-3">Dashboard</span>
                </a>
              </li>
              <li>
                <a
                  href="/user/history_absen"
                  className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faClock}
                  />
                  <span className="flex-1 ms-3 whitespace-nowrap ">Absen</span>
                </a>
              </li>
              <li>
                <a
                  href="/user/history_cuti"
                  className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faCalendarDays}
                  />
                  <span className="flex-1 ms-3 whitespace-nowrap">Cuti</span>
                </a>
              </li>
              <li>
                <a
                  href="/user/history_lembur"
                  className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faBusinessTime}
                  />
                  <span className="flex-1 ms-3 whitespace-nowrap">Lembur</span>
                </a>
              </li>
            </ul>
          )}
          {role === "SUPERADMIN" && (
            <ul>
              <li>
                <a
                  href="/superadmin/dashboard"
                  className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faCube}
                  />
                  <span className="ms-3">Dashboard</span>
                </a>
              </li>
              {/* // <!-- Dropdown Master Data --> */}
              <li>
                <button
                  type="button"
                  className="flex items-center w-full p-2 text-base text-blue-900 transition duration-75 rounded-lg group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                  // aria-controls="dropdown-masterdata"
                  // data-dropdown-toggle="dropdown-masterdata"
                  onClick={toggleMasterData}
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faUserTie}
                  />
                  <span className="flex-1 ml-3 text-left whitespace-nowrap">
                    Data Admin
                  </span>
                  <FontAwesomeIcon
                    icon={masterDataOpen ? faChevronUp : faChevronDown}
                    className="flex-shrink-0 w-4 h-4 text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                  />
                </button>
                <ul
                  // id="dropdown-masterdata"
                  className={`${
                    masterDataOpen ? "" : "hidden" // Tampilkan atau sembunyikan dropdown berdasarkan state masterDataOpen
                  } py-2 space-y-2`}
                >
                  {/* <!-- Menu superadmin --> */}
                  <li>
                    <a
                      href="/superadmin/admin"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faChalkboardUser}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Admin</span>
                    </a>
                  </li>
                  {/* <!-- Menu Organisasi --> */}
                  <li>
                    <a
                      href="/superadmin/organisasi"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBuilding}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">
                        Organisasi
                      </span>
                    </a>
                  </li>
                  {/* <!-- Menu Jabatan --> */}
                  <li>
                    <a
                      href="/superadmin/jabatan"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBriefcase}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Jabatan</span>
                    </a>
                  </li>

                  {/* <!-- Menu Jam Kerja --> */}
                  <li>
                    <a
                      href="/superadmin/shift"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faBusinessTime}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Shift</span>
                    </a>
                  </li>

                  {/* <!-- Menu Lokasi --> */}
                  <li>
                    <a
                      href="/superadmin/lokasi"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faMapLocationDot}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Lokasi</span>
                    </a>
                  </li>
                </ul>
              </li>
              {/* <!-- Dropdown user --> */}
              <li>
                <button
                  type="button"
                  className="flex items-center w-full p-2 text-base text-blue-900 transition duration-75 rounded-lg group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                  // aria-controls="dropdown-example"
                  // data-collapse-toggle="dropdown-example"
                  onClick={toggleRekapan}
                >
                  <FontAwesomeIcon
                    className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                    icon={faUserPlus}
                  />
                  <span className="flex-1 ml-3 text-left whitespace-nowrap">
                    Data User
                  </span>
                  <FontAwesomeIcon
                    icon={rekapanOpen ? faChevronUp : faChevronDown}
                    className="flex-shrink-0 w-4 h-4 text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                  />
                </button>

                <ul
                  // id="dropdown-masterdata"
                  className={`${
                    rekapanOpen ? "" : "hidden" // Tampilkan atau sembunyikan dropdown berdasarkan state masterDataOpen
                  } py-2 space-y-2`}
                >
                  {/* <!-- Menu Simpel --> */}
                  <li>
                    <a
                      href="/superadmin/data-user"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faUsers}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">User</span>
                    </a>
                  </li>
                  {/* <!-- Menu PerKaryawan --> */}
                  <li>
                    <a
                      href="/superadmin/absensi"
                      className="flex items-center w-full p-2 text-blue-900 transition duration-75 rounded-lg pl-11 group hover:bg-blue-100 dark:text-white dark:hover:bg-blue-700"
                    >
                      <FontAwesomeIcon
                        className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                        icon={faAddressCard}
                      />{" "}
                      <span className="flex-1 ml-3 whitespace-nowrap">Absensi</span>
                    </a>
                  </li>
                </ul>
              </li>
            </ul>
          )}
          {/* <li>
            <a
              href="/"
              onClick={(e) => {
                e.preventDefault();
                logout();
              }}
              className="flex items-center p-2 text-blue-900 rounded-lg dark:text-white hover:bg-blue-100 dark:hover:bg-blue-700 group"
            >
              <FontAwesomeIcon
                className="flex-shrink-0 w-5 h-5 text-blue-500 transition duration-75 dark:text-blue-400 group-hover:text-blue-900 dark:group-hover:text-white"
                icon={faArrowRightFromBracket}
              />
              <span className="flex-1 ms-3 whitespace-nowrap">Logout</span>
            </a>
          </li> */}
        </ul>
      </div>
    </aside>
  );
};

export default Sidebar;
