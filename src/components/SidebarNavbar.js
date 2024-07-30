import React, { useEffect, useRef, useState } from "react";
import Logo from "../components/absensii.png";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faAddressCard,
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

function SidebarNavbar() {
  const role = localStorage.getItem("role");
  const [isOpen, setIsOpen] = useState(false);
  const [masterDataOpen, setMasterDataOpen] = useState(false);
  const [rekapanOpen, setRekapanOpen] = useState(false);
  const [absenOpen, setAbsenOpen] = useState(false);
  const sidebarRef = useRef(null);

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

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };

  const handleClickOutside = (event) => {
    if (sidebarRef.current && !sidebarRef.current.contains(event.target)) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  return (
    <div>
      <button
        onClick={toggleSidebar}
        aria-controls="logo-sidebar"
        aria-expanded={isOpen}
        type="button"
        className="inline-flex items-center p-2 mt-2 ms-3 text-sm text-gray-500 rounded-lg sm:hidden hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:text-gray-400 dark:hover:bg-gray-700 dark:focus:ring-gray-600"
      >
        <span className="sr-only">Open sidebar</span>
        <svg
          className="w-6 h-6"
          aria-hidden="true"
          fill="currentColor"
          viewBox="0 0 20 20"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            clipRule="evenodd"
            fillRule="evenodd"
            d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 4.75zm0 10.5a.75.75 0 01.75-.75h7.5a.75.75 0 010 1.5h-7.5a.75.75 0 01-.75-.75zM2 10a.75.75 0 01.75-.75h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 10z"
          ></path>
        </svg>
      </button>

      <aside
        id="logo-sidebar"
        ref={sidebarRef}
        className={`fixed top-0 left-0 z-40 w-64 h-screen transition-transform ease-in-out duration-300 ${
          isOpen ? "translate-x-0" : "-translate-x-full"
        } sm:translate-x-0`}
        aria-label="Sidebar"
      >
        <div className="h-full flex flex-col">
          <div className={`bg-indigo-500 ${isOpen ? "hidden" : "block"}`}>
            <a href="" className="flex items-center p-3">
              <img src={Logo} className="h-11 me-3 text-white" alt="Logo" />
            </a>
          </div>
          <div className="bg-white flex-1 px-3 py-4">
            <nav className="">
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Jabatan
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Shift
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Lokasi
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Simpel
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Harian
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Bulanan
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Absensi
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Cuti
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Lembur
                            </span>
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
                        <span className="flex-1 ms-3 whitespace-nowrap ">
                          Absen
                        </span>
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
                        <span className="flex-1 ms-3 whitespace-nowrap">
                          Cuti
                        </span>
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
                        <span className="flex-1 ms-3 whitespace-nowrap">
                          Lembur
                        </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Admin
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Jabatan
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Shift
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Lokasi
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              User
                            </span>
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
                            <span className="flex-1 ml-3 whitespace-nowrap">
                              Absensi
                            </span>
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
            </nav>
          </div>
        </div>
      </aside>
    </div>
  );
}

export default SidebarNavbar;
