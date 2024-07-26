import React, { useEffect, useState } from "react";
import Sidebar from "../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faUsers,
  faClipboardUser,
  faCalendarDays,
} from "@fortawesome/free-solid-svg-icons";
import Navbar from "../../components/NavbarAdmin";
import axios from "axios";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../utils/api";

function Dashboard() {
  const [currentDateTime, setCurrentDateTime] = useState(new Date());
  const [userData, setUserData] = useState([]);
  const [absenData, setAbsenData] = useState([]);
  const [cutiData, setCutiData] = useState([]);
  const [jabatanData, setJabatanData] = useState([]);
  const [lokasiData, setLokasiData] = useState([]);
  const [organisasiData, setOrganisasiData] = useState([]);
  const [username, setUsername] = useState("");
  const idAdmin = localStorage.getItem("adminId");
  const adminId = localStorage.getItem("adminId");
  const [karyawan, setKaryawan] = useState("");

  const getallUser = async () => {
    try {
      const res = await axios.get(`${API_DUMMY}/api/user/${idAdmin}/users`);
      setKaryawan(res.data.length);
    } catch (error) {}
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentDateTime(new Date());
    }, 1000); // Update every second

    return () => clearInterval(interval);
  }, []);

  const addLeadingZero = (num) => (num < 10 ? "0" + num : num);

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

  const fetchData = async (url, setter) => {
    const token = localStorage.getItem("token");
    try {
      const response = await axios.get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setter(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const getUser = () =>
    fetchData(`${API_DUMMY}/api/user/get-allUser`, setUserData);
  const getAbsensi = () =>
    fetchData(`${API_DUMMY}/api/absensi/getAll`, setAbsenData);
  const getCuti = () => fetchData(`${API_DUMMY}/api/cuti/getall`, setCutiData);
  const getJabatan = () =>
    fetchData(`${API_DUMMY}/api/jabatan/getByAdmin/${adminId}`, setJabatanData);
  const getLokasi = () =>
    fetchData(`${API_DUMMY}/api/lokasi/get-admin/${idAdmin}`, setLokasiData);
  const getOrganisasi = () =>
    fetchData(
      `${API_DUMMY}/api/organisasi/all-by-admin/${idAdmin}`,
      setOrganisasiData
    );

  const getUsername = async () => {
    const token = localStorage.getItem("token");
    const id = localStorage.getItem("adminId");

    try {
      const response = await axios.get(`${API_DUMMY}/api/admin/getById/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setUsername(response.data.username);
    } catch (error) {
      console.error("Error fetching username:", error);
    }
  };

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
    getUser();
    getAbsensi();
    getCuti();
    getUsername();
    getJabatan();
    getLokasi();
    getOrganisasi();
    getallUser();
  }, []);

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
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="content-page container p-8 ml-0 md:ml-64 mt-12">
          <div className="mt-5 w-full">
            <div className="p-4 text-center bg-indigo-300 border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              <h2 className="text-2xl font-semibold mb-4 capitalize">
                Selamat Datang di Absensi
                <span> @{username}</span>
              </h2>
              <a className="profile-menu-link">{day}, </a>
              <a className="profile-menu-link active">{date} - </a>
              <a className="profile-menu-link">{time}</a>
            </div>
          </div>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-3 lg:gap-8 mt-12">
            <div className="pl-2 h-32 bg-indigo-500 rounded-lg shadow-md md:w-auto">
              <div className="flex w-full h-full py-2 px-4 bg-gray-100 rounded-lg justify-between">
                <div className="my-auto">
                  <p className="font-bold">User</p>
                  <p className="text-lg">Jumlah User</p>
                  <p className="text-lg">{userData.length}</p>
                </div>
                <div className="my-auto">
                  <FontAwesomeIcon icon={faUsers} size="2x" />
                </div>
              </div>
            </div>
            <div className="pl-2 h-32 bg-indigo-500 rounded-lg shadow-md md:w-auto">
              <div className="flex w-full h-full py-2 px-4 bg-gray-100 rounded-lg justify-between">
                <div className="my-auto">
                  <p className="font-bold">Absensi</p>
                  <p className="text-lg">Jumlah Absen</p>
                  <p className="text-lg">{absenData.length}</p>
                </div>
                <div className="my-auto">
                  <FontAwesomeIcon icon={faClipboardUser} size="2x" />
                </div>
              </div>
            </div>
            <div className="pl-2 h-32 bg-indigo-500 rounded-lg shadow-md md:w-auto">
              <div className="flex w-full h-full py-2 px-4 bg-gray-100 rounded-lg justify-between">
                <div className="my-auto">
                  <p className="font-bold">Cuti</p>
                  <p className="text-lg">Jumlah Cuti</p>
                  <p className="text-lg">{cutiData.length}</p>
                </div>
                <div className="my-auto">
                  <FontAwesomeIcon icon={faCalendarDays} size="2x" />
                </div>
              </div>
            </div>
          </div>

          <br />

          {/* Tabel Absensi */}
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                History absensi
              </h6>
            </div>
            <hr />
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
              <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-center text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Username
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Tanggal
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Jam Masuk
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Jam Pulang
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Kehadiran
                    </th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {absenData.map((absen, index) => (
                    <tr
                      key={index}
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4 capitalize">
                        {absen.user.username}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {formatDate(absen.tanggalAbsen)}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {absen.jamMasuk || "-"}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {absen.jamPulang || "-"}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {absen.statusAbsen}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <br />

          {/* Tabel Cuti */}
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                History Cuti
              </h6>
            </div>
            <hr />
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
              <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-center text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Username
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Awal Cuti
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Akhir Cuti
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Status
                    </th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {cutiData.map((cuti, index) => (
                    <tr
                      key={index}
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4 capitalize">
                        {cuti.user.username}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {formatDate(cuti.awalCuti)}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {formatDate(cuti.akhirCuti)}
                      </td>
                      <td className="px-6 py-4 capitalize">{cuti.status}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <br />

          {/* Tabel Jabatan */}
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Data Jabatan
              </h6>
            </div>
            <hr />
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
              <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-center text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Jabatan
                    </th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {jabatanData.map((jabatan, index) => (
                    <tr
                      key={index}
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4 capitalize">
                        {jabatan.namaJabatan}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <br />

          {/* Tabel Lokasi */}
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Data Lokasi
              </h6>
            </div>
            <hr />
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
              <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-center text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Nama Lokasi
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Alamat
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Jumlah Karyawan
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Organisasi
                    </th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {lokasiData.map((lokasi, index) => (
                    <tr
                      key={index}
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4 capitalize">
                        {lokasi.namaLokasi}
                      </td>
                      <td className="px-6 py-4 capitalize">{lokasi.alamat}</td>
                      <td className="px-6 py-4 capitalize">{karyawan}</td>
                      <td className="px-6 py-4 capitalize">
                        {lokasi.organisasi.namaOrganisasi}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <br />

          {/* Tabel Organisasi */}
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Data Organisasi
              </h6>
            </div>
            <hr />
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
              <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-center text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Nama Organisasi
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Alamat
                    </th>
                  </tr>
                </thead>
                <tbody className="text-center">
                  {organisasiData.map((organisasi, index) => (
                    <tr
                      key={index}
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4 capitalize">
                        {organisasi.namaOrganisasi}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {organisasi.alamat}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
