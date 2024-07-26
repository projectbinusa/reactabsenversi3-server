import React, { useEffect, useState } from "react";
import Sidebar from "../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faArrowRight,
  faArrowRightFromBracket,
  faSearch,
} from "@fortawesome/free-solid-svg-icons";
import {
  faCalendarDays,
  faCircleXmark,
  faClockFour,
} from "@fortawesome/free-regular-svg-icons";
import Navbar from "../../components/NavbarUser";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import axios from "axios";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../utils/api";

function Dashboard() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [currentDateTime, setCurrentDateTime] = useState(new Date());
  const [username, setUsername] = useState({});
  const [absensi, setAbsensi] = useState([]);
  const [cuti, setCuti] = useState([]);
  const [izin, setIzin] = useState([]);
  const [totalIzin, setTotalIzin] = useState(0);
  const [isAbsenMasuk, setIsAbsenMasuk] = useState(false);

  const getUsername = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/user/getUserBy/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setUsername(response.data);
    } catch (error) {
      console.error("Error fetching username:", error);
    }
  };

  const cekAbsensi = async () => {
    const userId = localStorage.getItem("userId");
    try {
      const absensiCheckResponse = await axios.get(
        `${API_DUMMY}/api/absensi/checkAbsensi/${userId}`
      );
      const isUserAlreadyAbsenToday =
        absensiCheckResponse.data ===
        "Pengguna sudah melakukan absensi hari ini.";
      if (isUserAlreadyAbsenToday) {
        setIsAbsenMasuk(true);
      }
    } catch (error) {
      console.error("Error checking absensi:", error);
    }
  };

  const getIzin = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/absensi/getizin/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setIzin(response.data);
      setTotalIzin(response.data.length);
    } catch (error) {
      console.error("Error fetching izin:", error);
    }
  };

  const getAbsensi = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/absensi/getByUserId/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setAbsensi(response.data);
    } catch (error) {
      console.error("Error fetching absensi:", error);
    }
  };

  const getCuti = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/cuti/getByUser/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setCuti(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentDateTime(new Date());
    }, 1000);

    getUsername();
    getAbsensi();
    getCuti();
    getIzin();
    cekAbsensi();

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    setUsername(username); // Setelah mendapatkan respons, atur username
  }, [username]); // Tambahkan username sebagai dependensi

  // Fungsi untuk menambah nol di depan angka jika angka kurang dari 10
  const addLeadingZero = (num) => {
    return num < 10 ? "0" + num : num;
  };

  // Mendapatkan informasi hari, tanggal, dan waktu
  const day = currentDateTime.toLocaleDateString("id-ID", { weekday: "long" });
  const date = currentDateTime.toLocaleDateString("id-ID", {
    day: "numeric",
    month: "long",
    year: "numeric",
  });
  const time =
    addLeadingZero(currentDateTime.getHours()) +
    ":" +
    addLeadingZero(currentDateTime.getMinutes()) +
    ":" +
    addLeadingZero(currentDateTime.getSeconds());

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  // Function to format date in Indonesian
  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  useEffect(() => {
    if (localStorage.getItem("loginSuccess") === "true") {
      Swal.fire({
        icon: "success",
        title: "Berhasil masuk!",
      });
      localStorage.removeItem("loginSuccess");
    }
  }, []);
  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar toggleSidebar={toggleSidebar} />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar isOpen={sidebarOpen} />
        </div>
        <div className="content-page container p-8 min-h-screen ml-0 md:ml-64 mt-5">
          <div className="mt-12 bg-slate-200 p-5 rounded-xl shadow-xl">
            <h1 className="judul text-3xl font-semibold text-center capitalize">
              Selamat Datang @{username.username}
            </h1>

            <div className="text-lg text-center mt-2 text-black">
              {day}, {date} - {time}
            </div>

            <div className="grid grid-cols-1 gap-4 lg:grid-cols-3 lg:gap-8 mt-7">
              <Link to={isAbsenMasuk ? "#" : "/user/absen"}>
                <div
                  className={`pl-2 h-24 rounded-lg shadow-md md:w-auto ${
                    isAbsenMasuk
                      ? "bg-gray-500 cursor-not-allowed"
                      : "bg-blue-500"
                  }`}
                >
                  <div className="flex w-full h-full py-2 px-4 bg-white rounded-lg justify-between">
                    <div className="my-auto">
                      <p
                        className={`font-bold ${
                          isAbsenMasuk ? "text-gray-400" : "text-black"
                        }`}
                      >
                        Masuk
                      </p>
                      <p
                        className={`text-lg ${
                          isAbsenMasuk ? "text-gray-400" : "text-black"
                        }`}
                      >
                        Absen masuk.
                      </p>
                    </div>
                    <div
                      className={`my-auto ${
                        isAbsenMasuk ? "text-gray-400" : "text-black"
                      }`}
                    >
                      <FontAwesomeIcon
                        icon={faArrowRightFromBracket}
                        size="2x"
                      />
                    </div>
                  </div>
                </div>
              </Link>

              <Link to="/user/pulang">
                <div className="pl-2 h-24 bg-green-500 rounded-lg shadow-md md:w-auto">
                  <div className="flex w-full h-full py-2 px-4 bg-white rounded-lg justify-between">
                    <div className="my-auto">
                      <p className="font-bold text-black">Pulang</p>
                      <p className="text-lg text-black">Absen pulang.</p>
                    </div>
                    <div className="my-auto text-black">
                      <FontAwesomeIcon
                        icon={faArrowRightFromBracket}
                        size="2x"
                      />
                    </div>
                  </div>
                </div>
              </Link>

              <Link to="/user/izin">
                <div className="pl-2 h-24 bg-red-500 rounded-lg shadow-md md:w-auto">
                  <div className="flex w-full h-full py-2 px-4 bg-white rounded-lg justify-between">
                    <div className="my-auto">
                      <p className="font-bold text-black">Izin</p>
                      <p className="text-lg text-black">Ajukan Izin.</p>
                    </div>
                    <div className="my-auto text-black">
                      <FontAwesomeIcon icon={faCircleXmark} size="2x" />
                    </div>
                  </div>
                </div>
              </Link>
            </div>

            <div className="flex justify-center mt-3 gap-4 flex-col md:flex-row">
              <Link to="/user/cuti">
                <div className="pl-2 h-24 w-full md:w-80 bg-red-400 rounded-lg shadow-md md:mr-20 cursor-pointer">
                  <div className="flex w-full h-full py-2 px-4 bg-white rounded-lg justify-between">
                    <div className="my-auto">
                      <p className="font-bold text-black">Cuti</p>
                      <p className="text-lg text-black">Ajukan cuti.</p>
                    </div>
                    <div className="my-auto text-black">
                      <FontAwesomeIcon icon={faCalendarDays} size="2x" />
                    </div>
                  </div>
                </div>
              </Link>

              <Link to="/user/lembur">
                <div className="pl-2 h-24 w-full md:w-80 bg-yellow-300 rounded-lg shadow-md md:ml-0 mt-4 md:mt-0 cursor-pointer">
                  <div className="flex w-full h-full py-2 px-4 bg-white rounded-lg justify-between">
                    <div className="my-auto">
                      <p className="font-bold text-black">Lembur</p>
                      <p className="text-lg text-black">Ajukan lembur.</p>
                    </div>
                    <div className="my-auto text-black">
                      <FontAwesomeIcon icon={faClockFour} size="2x" />
                    </div>
                  </div>
                </div>
              </Link>
            </div>
          </div>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-2 lg:gap-8 mt-12">
            <div className="bg-blue-500 rounded-lg shadow-md p-4 md:w-full lg:w-auto">
              <div className="flex justify-between items-center">
                <div>
                  <p className="text-white font-bold text-lg">Total Absen</p>
                  <p className="text-white text-md">
                    Jumlah absen yang tercatat
                  </p>
                </div>
                <div className="text-white text-2xl font-semibold">
                  {absensi.length}
                </div>
              </div>
            </div>

            <div className="bg-red-500 rounded-lg shadow-md p-4 md:w-full lg:w-auto">
              <div className="flex justify-between items-center">
                <div>
                  <p className="text-white font-bold text-lg">Total Izin</p>
                  <p className="text-white text-md">
                    Jumlah izin yang diajukan
                  </p>
                </div>
                <div className="text-white text-2xl font-semibold">
                  {totalIzin}
                </div>
              </div>
            </div>
          </div>

          <div className="tabel-absen mt-12 bg-blue-100 p-5 rounded-xl shadow-xl border border-gray-300">
            <h2 className="text-xl font-bold text-black">History Absensi</h2>
            <div className="overflow-x-auto rounded-lg border border-gray-200 mt-4">
              <table className="min-w-full divide-y-2 divide-gray-200 bg-white text-sm border border-gray-300">
                <thead className="text-left text-white bg-blue-500">
                  <tr>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      NO
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      TANGGAL
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      KEHADIRAN
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-200">
                  {absensi.map((absenData, index) => (
                    <tr key={index}>
                      <td className="whitespace-nowrap px-4 py-2 font-medium text-gray-900 text-center">
                        {index + 1}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {formatDate(absenData.tanggalAbsen)}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {absenData.statusAbsen}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="flex justify-end mt-5">
              <button
                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
                onClick={() => (window.location.href = "/user/history_absen")}
              >
                <FontAwesomeIcon icon={faArrowRight} />
              </button>
            </div>
          </div>

          <div className="tabel-cuti mt-12 bg-blue-100 p-5 rounded-xl shadow-xl border border-gray-300">
            <h2 className="text-xl font-bold text-black">Permohonan Cuti</h2>
            <div className="overflow-x-auto rounded-lg border border-gray-200 mt-4">
              <table className="min-w-full divide-y-2 divide-gray-200 bg-white text-sm border border-gray-300">
                <thead className="text-left text-white bg-blue-500">
                  <tr>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      NO
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      KEPERLUAN CUTI
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-200">
                  {cuti.map((item, index) => (
                    <tr key={index}>
                      <td className="whitespace-nowrap px-4 py-2 font-medium text-gray-900 text-center">
                        {index + 1}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {item.keperluan}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="flex justify-end mt-5">
              <button
                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
                onClick={() => (window.location.href = "/user/history_cuti")}
              >
                <FontAwesomeIcon icon={faArrowRight} />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
