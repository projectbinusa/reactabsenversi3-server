import React, { useEffect, useState } from "react";
import Navbar from "../../components/NavbarUser";
import Sidebar from "../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { API_DUMMY } from "../../utils/api";

function DetailAbsen() {
  const [absensi, setAbsensi] = useState([]);
  const { id } = useParams();

  const getAbsensi = async () => {
    const token = localStorage.getItem("token");

    try {
      const absensiResponse = await axios.get(
        `${API_DUMMY}/api/absensi/getData/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (
        typeof absensiResponse.data === "object" &&
        absensiResponse.data !== null
      ) {
        const item = absensiResponse.data;
        const formattedAbsensi = {
          id: item.id,
          tanggalAbsen: item.tanggalAbsen,
          jamMasuk: item.jamMasuk,
          lokasiMasuk: item.lokasiMasuk,
          lokasiPulang: item.lokasiPulang,
          keteranganPulang: item.keteranganPulang,
          keteranganIzin: item.keteranganIzin,
          keteranganPulangAwal: item.keteranganPulangAwal,
          jamPulang: item.jamPulang,
          keteranganTerlambat: item.keteranganTerlambat,
          fotoMasuk: item.fotoMasuk,
          fotoPulang: item.fotoPulang,
          status: item.status,
          statusAbsen: item.statusAbsen,
          user: {
            id: item.user.id,
            email: item.user.email,
            username: item.user.username,
            organisasi: item.user.organisasi,
            jabatan: item.user.jabatan,
            shift: item.user.shift,
            admin: item.user.admin,
            role: item.user.role,
          },
        };

        setAbsensi([formattedAbsensi]);
      } else {
        console.error("Expected an array, but received:", absensiResponse.data);
      }
    } catch (error) {
      console.error("Error fetching absensi:", error);
    }
  };

  useEffect(() => {
    getAbsensi();
  }, []);

  const formatDate = (dateString) => {
    const options = {
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className=" sm:ml-64 content-page container p-8  ml-14 md:ml-64 mt-12">
          <div className="p-4">
            <div className="p-5 mt-5">
              {/* <!-- Card --> */}
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                {/* <!-- Header --> */}
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Detail Absen
                  </h6>
                </div>
                <div className="mt-7 text-left">
                  <hr />
                </div>
                <div className="mt-7 text-left">
                  {absensi.map((item, index) => (
                    <div key={index} className="grid md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="username"
                          id="username"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={item.user.username}
                          required
                        />
                        <label
                          htmlFor="username"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Username
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="tanggal"
                          id="tanggal"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={formatDate(item.tanggalAbsen)}
                          required
                        />
                        <label
                          htmlFor="tanggal"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Tanggal
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="jam_masuk"
                          id="jam_masuk"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={item.jamMasuk}
                          required
                        />
                        <label
                          htmlFor="jam_masuk"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Jam Masuk
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="jam_pulang"
                          id="jam_pulang"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={item.jamPulang}
                          required
                        />
                        <label
                          htmlFor="jam_pulang"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Jam Pulang
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="lokasi"
                          id="lokasi"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={item.lokasiMasuk ? item.lokasiMasuk : "-"}
                          required
                        />
                        <label
                          htmlFor="lokasi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Lokasi Masuk
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="lokasi"
                          id="lokasi"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={item.lokasiPulang ? item.lokasiPulang : "-"}
                          required
                        />
                        <label
                          htmlFor="lokasi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Lokasi Pulang
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <img
                          className="max-width-60 max-height-70 mt-10"
                          style={{ marginBottom: "25px", marginLeft: "5px" }}
                          src={item.fotoMasuk ? item.fotoMasuk : "-"}
                          alt="Foto Masuk"
                        />
                        <label
                          htmlFor="foto"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Foto Masuk:
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <img
                          className="max-width-100 max-height-70 mt-10"
                          style={{ marginBottom: "25px", marginLeft: "5px" }}
                          src={item.fotoPulang ? item.fotoPulang : "-"}
                          alt="Foto Pulang"
                        />
                        <label
                          htmlFor="foto"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Foto Pulang:
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="statusabsen"
                          id="statusabsen"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          onChange={() => {}}
                          value={item.statusAbsen}
                          required
                        />
                        <label
                          htmlFor="statusabsen"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Status Absen
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="keterangan"
                          id="keterangan"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=""
                          onChange={() => {}}
                          value={
                            item.keteranganTerlambat
                              ? item.keteranganTerlambat
                              : "-"
                          }
                          required
                        />
                        <label
                          htmlFor="keterangan"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Keterangan Terlambat
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="keterangan"
                          id="keterangan"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=""
                          onChange={() => {}}
                          value={
                            item.keteranganPulangAwal
                              ? item.keteranganPulangAwal
                              : "-"
                          }
                          required
                        />
                        <label
                          htmlFor="keterangan"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Keterangan Pulang Awal
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="keterangan"
                          id="keterangan"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=""
                          onChange={() => {}}
                          value={
                            item.keteranganIzin ? item.keteranganIzin : "-"
                          }
                          required
                        />
                        <label
                          htmlFor="keterangan"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Keterangan Izin
                        </label>
                      </div>
                    </div>
                  ))}
                </div>

                <div className=" text-left mt-4">
                  {/* <!-- email & username Input --> */}
                  <div className="grid grid-cols-2 gap-4">
                    <div className="relative z-0 w-full mb-6 group">
                      <div className="flex justify-between">
                        <a
                          className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                          href="/user/history_absen"
                        >
                          <FontAwesomeIcon icon={faArrowLeft} />
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DetailAbsen;
